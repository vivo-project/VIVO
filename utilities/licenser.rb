=begin
--------------------------------------------------------------------------------

Create a copy of the source files, with licensing information inserted.

--------------------------------------------------------------------------------
2010-01-26 initial version J.Blake
--------------------------------------------------------------------------------
=end

require 'date'
require 'fileutils'

class LicenserStats
  attr_reader :substitutions
  attr_reader :missing_tags
  attr_reader :known_exceptions
  attr_reader :file_count
  attr_reader :dir_count

  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  def which_match(filename)
    @file_matchers.each do |matcher|
      return matcher if File.fnmatch(matcher, filename)
    end
    raise("filename matches no matchers!: #{filename}")
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(root_dir, file_matchers, full)
    @root_dir = "#{root_dir}/".gsub('//', '/')
    @file_matchers = file_matchers
    @full = full

    # keep track of how many substitutions for all file types
    @substitutions = Hash.new()
    file_matchers.each do |matcher|
      @substitutions[matcher] = 0
    end

    # keep track of missing tags, only in file types that have missing tags
    @missing_tags =  Hash.new(0)

    # keep track of how many known non-licensed files we encounter, and what types.
    @known_exceptions =  Hash.new(0)

    # keep track of how many files are copied
    @file_count = 0
    
    #keep track of how many directories are copied
    @dir_count = 0
  end

  def enter_directory(path)
    @dir_count += 1
    puts "Entering directory: #{path}" if @full
  end

  def record_scan_non_matching(filename)
    @file_count += 1
    puts "    Scan without mods: #{filename}" if @full
  end

  def record_copy_non_matching(filename)
    @file_count += 1
    puts "    Copy without mods: #{filename}" if @full
  end

  def record_scan_matching(filename)
    @file_count += 1
    puts "    Scan with mods: #{filename}" if @full
  end

  def record_copy_matching(filename)
    @file_count += 1
    puts "    Copy with mods: #{filename}" if @full
  end

  def record_known_exception(filename)
    @file_count += 1
    puts "    Known exception:              #{filename}" if @full
    @known_exceptions[which_match(filename)] += 1
  end

def record_tag(filename)
    puts "    Substituted license text into #{filename}" if @full
    @substitutions[which_match(filename)] += 1
  end

  def record_no_tag(filename, source_path)
    puts "WARN: Found no license tag in #{source_path.sub(@root_dir, '')}"
    @missing_tags[which_match(filename)] += 1
  end
end

class Licenser

  MAGIC_STRING = '$This file is distributed under the terms of the license in /doc/license.txt$'

  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Prepare the license as an array of lines of text,
  # with the current year substituted in for ${year}
  #
  def prepare_license_text(license_file)
    year_string = DateTime.now.year.to_s
    text = []
    File.open(license_file) do |file|
      file.each do |line|
        text << line.gsub('${year}', year_string)
      end
    end
    return text
  end
  
  # The globs in the exceptions file are assumed to be 
  # relative to the source directory. Make them explicitly so.
  #
  # Ignore any blank lines or lines that start with a '#'
  #
  def prepare_exception_globs(exceptions_file, source_dir)
    globs = []
    File.open(exceptions_file) do |file|
      file.each do |line|
        glob = line.strip
        if (glob.length > 0) && (glob[0..0] != '#')
          globs << "#{source_dir}/#{glob}".gsub('//', '/')
        end
      end
    end
    return globs
  end

  # Recursively scan this directory, and copy if we are not scan-only.
  #
  def scan_dir(source_dir, target_dir)
    @stats.enter_directory(source_dir)

    Dir.mkdir(target_dir) if !@scan_only
    
    Dir.foreach(source_dir) do |filename|
      source_path = "#{source_dir}/#{filename}"
      target_path = "#{target_dir}/#{filename}"

      # What kind of beast is this?
      if filename == '.' || filename == '..'
        is_skipped_directory = true
      else
        if File.directory?(source_path)
          is_directory = true
        else
          if filename_matches_pattern?(filename)
            if path_matches_exception?(source_path)
              is_exception = true
            else
              is_match = true
            end
          else
            is_ignored = true
          end
        end
      end
      

      if is_skipped_directory
        # do nothing
      elsif is_directory
        scan_dir(source_path, target_path)
      elsif is_match
        if @scan_only
          @stats.record_scan_matching(filename)
          scan_file(source_path, filename)
        else
          @stats.record_copy_matching(filename)
          copy_file_with_license(source_path, target_path, filename)
        end
      elsif is_exception
        @stats.record_known_exception(filename)
        if @scan_only
          # do nothing
        else
          copy_file_without_license(source_path, target_path)
        end
      else # not a match
        if @scan_only
          @stats.record_scan_non_matching(filename)
        else
          @stats.record_copy_non_matching(filename)
          copy_file_without_license(source_path, target_path)
        end
      end
    end
  end

  # Does this file path match any of the exceptions?
  #
  def path_matches_exception?(path)
    @known_exceptions.each do |pattern|
      return true if File.fnmatch(pattern, path)
    end
    return false
  end

  # Does this filename match any of the patterns?
  #
  def filename_matches_pattern?(filename)
    @file_matchers.each do |pattern|
      return true if File.fnmatch(pattern, filename)
    end
    return false
  end
  
  # This file would be eligible for licensing if we weren't in scan-only mode.
  #
  def scan_file(source_path, filename)
    found = 0
    File.open(source_path) do |source_file|
      source_file.each do |line|
        if line.include?(MAGIC_STRING)
          found += 1
        end
      end
    end

    if found == 0
      @stats.record_no_tag(filename, source_path)
    elsif found == 1
      @stats.record_tag(filename)
    else
      raise("File contains #{found} license lines: #{source_path}")
    end
  end

  # This file matches at least one of the file-matching strings, and does not
  # match any exceptions. Replace the magic string with the license text.
  #
  def copy_file_with_license(source_path, target_path, filename)
    found = 0
    File.open(source_path) do |source_file|
      File.open(target_path, "w") do |target_file|
        source_file.each do |line|
          if line.include?(MAGIC_STRING)
            found += 1
            insert_license_text(target_file, line)
          else
            target_file.print line
          end
        end
      end
    end

    if found == 0
      @stats.record_no_tag(filename, source_path)
    elsif found == 1
      @stats.record_tag(filename)
    else
      raise("File contains #{found} license lines: #{source_path}")
    end
  end

  # Figure out the comment characters and write the license text to the file.
  #
  def insert_license_text(target_file, line)
    ends = line.split(MAGIC_STRING)
    if ends.size != 2
      raise ("Can't parse this license line: #{line}")
    end

    target_file.print "#{ends[0].strip}\n"

    @license_text.each do |text|
      target_file.print "#{text.rstrip}\n"
    end

    target_file.print "#{ends[1].strip}\n"
  end
  
  # This file either doesn't match any of the file-matching strings, or
  # matches an exception
  #
  def copy_file_without_license(source_path, target_path)
    FileUtils.cp(source_path, target_path)
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  # Setup and get ready to process.
  # * source_dir is a String -- the path to the top level directory to be copied
  # * target_dir is a String -- the path to the top level directory to copy into
  #      (must not exist, but its parent must exist!)(ignored if scan_only is set)
  # * file_matchers is an array of Strings -- filename globs that match the files we 
  #      want to license.
  # * license_file is a String -- the path to the text of the license agreement
  #      (with a ${year} token in it)
  # * known_exceptions_file is a String -- the path to a list of filename/path globs 
  #      that match the files that we know should have no license tags in them.
  # * scan_only is a Boolean -- if true, we scan the entire source dir without copying, 
  #      and target_dir is ignored.
  # * full_report is a Boolean -- if true, we give a full log instead of just a summary.
  #
  def initialize(source_dir, target_dir, file_matchers, license_file, known_exceptions_file, scan_only, full_report)
    if !File.exist?(source_dir)
      raise "Source directory does not exist: #{source_dir}"
    end

    if !scan_only 
      if File.exist?(target_dir)
        raise "Target directory already exists: #{target_dir}"
      end
      
      target_parent = File.dirname(target_dir)
      if !File.exist?(target_parent)
        raise "Path to target directory doesn't exist: #{target_parent}"
      end
    end
    
    if !File.exist?(license_file)
      raise "License file does not exist: #{license_file}"
    end

    if !File.exist?(known_exceptions_file)
      raise "Known exceptions file does not exist: #{known_exceptions_file}"
    end

    @source_dir = source_dir
    @target_dir = target_dir
    
    @file_matchers = file_matchers

    @license_file = license_file
    @license_text = prepare_license_text(license_file)

    @known_exceptions_file = known_exceptions_file
    @known_exceptions = prepare_exception_globs(known_exceptions_file, source_dir)
    
    @scan_only = scan_only
    
    @full_report = full_report
    @stats = LicenserStats.new(source_dir, file_matchers, full_report)
  end

  # Start the recursive scanning (and copying).
  def process()
    scan_dir(@source_dir, @target_dir)
  end

  # Report the summary statistics
  def report()
    puts "Licenser: run completed at #{DateTime.now.strftime("%H:%M:%S on %b %d, %Y")}"
    puts "          copied #{@stats.file_count} files in #{@stats.dir_count} directories."
    puts
    puts 'Substitutions'
    @stats.substitutions.sort.each do |line|
      printf("%5d %s\n", line[1], line[0])
    end
    puts
    puts 'Known non-licensed files'
    @stats.known_exceptions.sort.each do |line|
      printf("%5d %s\n", line[1], line[0])
    end
    puts
    puts 'Missing tags'
    @stats.missing_tags.sort.each do |line|
      printf("%5d %s\n", line[1], line[0])
    end
    puts
    puts 'parameters:'
    puts "    source_dir = #{@source_dir}"
    puts "    target_dir = #{@target_dir}"
    puts "    file_matchers = #{@file_matchers.join(', ')}"
    puts "    license_file = #{@license_file}"
    puts "    known_exceptions_file = #{@known_exceptions_file}"
    puts "    scan_only = #{@scan_only}"
    puts "    full_report = #{@full_report}"
  end
end


# ------------------------------------------------------------------------
# Main routine
# ------------------------------------------------------------------------


# BOGUS test harness
=begin
source_dir = '/Vivoweb_Stuff/Testing_licenser/sourceDir'
target_dir = '/Vivoweb_Stuff/Testing_licenser/targetDir'
license_file = "#{File.dirname(File.dirname(File.expand_path(__FILE__)))}/doc/license.txt"
known_exceptions_file = '/Vivoweb_Stuff/Testing_licenser/known_exceptions.txt'
full_report = true;
scan_only = true;
=end


source_dir = File.dirname(File.dirname(File.expand_path(__FILE__)))
license_file = "#{source_dir}/doc/license.txt"
known_exceptions_file = "#{source_dir}/utilities/known_exceptions.txt"
full_report = false;

if ARGV.length == 0
  scan_only = true;
  target_dir = "";
else
  scan_only = false;
  target_dir = ARGV[0]
end

file_matchers = ['*.java', '*.jsp', '*.tld', '*.xsl', '*.xslt', '*.css', '*.js', 'build.xml']

l = Licenser.new(source_dir, target_dir, file_matchers, license_file, known_exceptions_file, scan_only, full_report)
l.process
l.report
