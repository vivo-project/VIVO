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

NAMESPACES_FILENAME = 'file_storage_namespaces.properties'

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
  # Look in the file_info file and find the default namespace. Lines are in 
  # this form:
  #      Uv::http://vivo.scripps.edu/individual/n2938:     Lv:0::_main_image_testtubes.jpg:
  #
  # The first line may be a header line, so use the second one.
  #
  def find_default_namespace()
    second_line = IO.readlines(@file_info_file)[1]
    match = /Uv::(.*)individual/.match(second_line)
    raise "Can't find default namespace: '#{second_line}'" if match == nil
    @default_namespace = "#{match.captures[0]}individual/"
    puts "default namespace is '#{@default_namespace}'" 
  end
  
  def adjust_namespaces_file()
    namespaces = read_namespaces_file() 
    namespaces = add_namespace_if_needed(namespaces)
    write_namespaces_file(namespaces)
    
    namespaces.each() do |key, value|
      if value == @default_namespace
        @prefix = key
        puts "prefix is #{@prefix}"
        break
      end
    end
  end
  
  def read_namespaces_file()
    Dir.chdir(@uploads_directory) do |dir|
      namespaces = {}
      if File.file?(NAMESPACES_FILENAME) 
        namespaces = PropertyFileReader.read(NAMESPACES_FILENAME)
        namespaces.delete("properties_file_path")
        puts "namespaces is already set to '#{namespaces}'"
      end
      return namespaces
    end
  end    
  
  def add_namespace_if_needed(namespaces)      
    if namespaces.has_value?(@default_namespace) 
      puts "found prefix"
    else
      @prefix = ''
        'abcdefghijklmnopqrstuvwxyz'.chars() do |this_char|
        if (!namespaces.has_key?(this_char)) 
          namespaces[@prefix] = @default_namespace
          puts "assigned prefix = '#{@this_char}'"
          break
        end
      end
    end
    return namespaces
  end
  
  def write_namespaces_file(namespaces)
    if @scan_only
      puts "Scan-only: not writing namespaces file"
    else
      Dir.chdir(@uploads_directory) do |dir|
        File.open(NAMESPACES_FILENAME, "w") do |f|
          namespaces.each do |prefix, namespace| 
            f.puts "#{prefix} = #{namespace}"
          end
        end 
      end
    end
  end  
  
  #
  # Second pass -- figure out the location for each file. If the file 
  #                doesn't exist, copy the template to that location.
  #
  def second_pass()
    File.open(@file_info_file) do |f|
      f.each() do |line|
        next if header_line?(line.chomp) 
        process_file_info_line(line.chomp)
      end
    end
  end
  
  def header_line?(line)
    return false unless line.match("^bytestreamUri")
    
    puts "Skipping header line: '#{line}'"
    return true
  end
  
  def process_file_info_line(line)
    uri, filename = parse_info_line(line)
    puts "URI is '#{uri}'"
    puts "Filename is '#{filename}'"
    prefixed_uri = substitute_prefix_for_namespace(uri)
    full_path = construct_full_path(prefixed_uri, filename)
    puts "Full path is '#{full_path}'"
    
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
  
  def parse_info_line(line)
    # Lines are in this form:    Uv::URI:     Lv:0::FILENAME:
    match = line.match(/Uv::(.*):\s+Lv:0::(.*):/)
    raise "Can't parse this line: '#{line}'" if !match
    return match.captures[0], match.captures[1]
  end
  
  def substitute_prefix_for_namespace(uri)
    if uri[0, @default_namespace.length] == @default_namespace
      return uri.sub(@default_namespace, "#{@prefix}~")
    else  
      raise "Doesn't start with default namespace: '#{uri}'"
    end
  end
  
  def construct_full_path(prefixed_uri, filename)
    path = ""
    0.step(prefixed_uri.size - 1, 3) do |i|
      path = "#{path}/#{prefixed_uri[i, 3]}"
    end
    puts "path is: '#{path}'"
    return "#{@uploads_directory}/file_storage_root#{path}/#{filename}"
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------
  
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
  # Start the recursive scanning (and copying).
  #
  def process()
    find_default_namespace()
    adjust_namespaces_file()
    second_pass()
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
