#! /usr/bin/ruby

=begin
--------------------------------------------------------------------------------

Take a file that MySQL produced listing the URIs and filenames of all expected
upload files. Scan through the uploads directory, creating such files wherever
they are needed.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end
$: << File.dirname(File.expand_path(__FILE__))
require 'date'
require 'fileutils'
require 'property_file_reader'


# ------------------------------------------------------------------------------------
# FileInfoFile class
# ------------------------------------------------------------------------------------

class FileInfoFile
  attr_reader :default_namespace
  attr_reader :data
  
  def parse_info_line(line)
    # Lines are in this form:    "URI","filename"
    match = line.match(/^"([^"]*)","([^"]*)"$/)
    raise "Can't parse this line: '#{line}'" if !match
    return match.captures[0], match.captures[1]
  end
  
  def parse_default_namespace(uri)
    match = /^(.*)individual/.match(uri)
    raise "Can't find default namespace: '#{uri}'" if match == nil
    "#{match.captures[0]}individual/"
  end
  
  public
  
  def initialize(filepath)
    @data = []
    File.open(filepath) do |f|
      f.each() do |line|
        @data.push(parse_info_line(line))
      end
    end
    puts "parsed #{@data.length} lines."
    
    @default_namespace = parse_default_namespace(@data[0][0]) 
    puts "default namespace is '#{@default_namespace}'" 
  end
end

# ------------------------------------------------------------------------------------
# NamespacesFile class
#
# Read, query, modify and write the namespace-prefixes file.
# ------------------------------------------------------------------------------------

class NamespacesFile
  NAMESPACES_FILENAME = 'file_storage_namespaces.properties'
  
  public
  
  def initialize(uploads_directory, scan_only)
    @uploads_directory = uploads_directory
    @scan_only = scan_only
    
    Dir.chdir(@uploads_directory) do |dir|
      @namespaces = {}
      if File.file?(NAMESPACES_FILENAME) 
        @namespaces = PropertyFileReader.read(NAMESPACES_FILENAME)
        @namespaces.delete("properties_file_path")
      end
    end
  end
  
  def add_namespace(namespace)
    if @namespaces.has_value?(namespace) 
      puts "found prefix for #{namespace}"
      return
    end

    'abcdefghijklmnopqrstuvwxyz'.split("").each do |this_char|
      if (!@namespaces.has_key?(this_char))
        @namespaces[this_char] = namespace
        puts "assigned prefix = '#{this_char}'"
        return
      end
    end
    raise "all prefixes are used!"
  end
  
  def prefix(namespace)
    @namespaces.each() do | key, value |
      return key if value == namespace
    end
    raise "no prefix for '#{namespace}'"
  end
  
  def write()
    if @scan_only
      puts "Scan-only: not writing namespaces file"
    else
      Dir.chdir(@uploads_directory) do |dir|
        File.open(NAMESPACES_FILENAME, "w") do |f|
          @namespaces.each do |prefix, namespace| 
            f.puts "#{prefix} = #{namespace}"
          end
        end 
      end
    end
  end
end

# ------------------------------------------------------------------------------------
# Main class - UploadFileFaker
# ------------------------------------------------------------------------------------

class UploadFileFaker
  #
  # Do we have any chance of succeeding with these properties?
  #
  def sanity_checks_on_properties()
    raise("Properties file must contain a value for 'uploads_directory'") if @uploads_directory == nil
    raise("Properties file must contain a value for 'file_info_file'") if @file_info_file == nil
    raise("Properties file must contain a value for 'template_file'") if @template_file == nil
    
    if !File.directory?(@uploads_directory)
      raise "Not a directory: '#{@uploads_directory}'."
    end
    if !File.file?(@file_info_file)
      raise "File does not exist: '#{@file_info_file}'."
    end
    if !File.file?(@template_file)
      raise "File does not exist: '#{@template_file}'."
    end
  end
  
  #
  # Check each location that should contain an image, and if we're not just 
  # scanning, put one there.
  #
  def create_image_files_where_needed()
    @file_info.data.each do |line|
      uri, filename = line
      process_file_info(uri, filename)
    end
  end
  
  def process_file_info(uri, filename)
    full_path = figure_full_path(uri, filename)
    
    if File.file?(full_path)
      puts "File already exists at: '#{full_path}'"
    elsif @scan_only
      puts "Scan only - no file at: '#{full_path}'"
    else
      puts "Creating file at:       '#{full_path}'"
      FileUtils.mkpath(File.dirname(full_path))
      FileUtils.cp(@template_file, full_path)
    end
  end

  def figure_full_path(uri, filename) 
    prefixed_uri = substitute_prefix_for_namespace(uri)
    construct_full_path(prefixed_uri, filename)
  end
      
  def substitute_prefix_for_namespace(uri)
    if uri[0, @namespace.length] == @namespace
      uri.sub(@namespace, "#{@prefix}~")
    else  
      raise "Doesn't start with default namespace: '#{uri}'"
    end
  end
  
  def construct_full_path(prefixed_uri, filename)
    path = ""
    0.step(prefixed_uri.size - 1, 3) do |i|
      path = "#{path}/#{prefixed_uri[i, 3]}"
    end
    "#{@uploads_directory}/file_storage_root#{path}/#{filename}"
  end

  public
  
  #
  # Setup and get ready to process.
  #
  # properties is a map of keys to values, probably parsed from a properties file.
  #
  def initialize(properties)
    scan_only_string = properties['scan_only']
    @scan_only = 'false' != scan_only_string
    
    @uploads_directory = properties['uploads_directory']
    @file_info_file = properties['file_info_file']
    @template_file = properties['template_file']
    
    sanity_checks_on_properties()
  end
  
  #
  # Start the scanning (and copying).
  #
  def process()
    @file_info = FileInfoFile.new(@file_info_file)
    @namespace = @file_info.default_namespace
    
    namespaces_file = NamespacesFile.new(@uploads_directory, @scan_only)
    namespaces_file.add_namespace(@namespace)
    namespaces_file.write()
    @prefix = namespaces_file.prefix(@namespace)
    
    create_image_files_where_needed()
  end
end

#
# ------------------------------------------------------------------------------------
# Standalone calling.
#
# Do this if this program was called from the command line. That is, if the command
# expands to the path of this file.
# ------------------------------------------------------------------------------------
#

if File.expand_path($0) == File.expand_path(__FILE__)
  if ARGV.length == 0
    raise("No arguments - usage is: UploadFileFaker.rb <property_file>")
  end
  if !File.file?(ARGV[0])
    raise "File does not exist: '#{ARGV[0]}'."
  end
  
  properties = PropertyFileReader.read(ARGV[0])
  
  uff = UploadFileFaker.new(properties)
  uff.process
  
  puts "UploadFileFaker was successful."
end

