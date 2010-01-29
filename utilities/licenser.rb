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

  def initialize(file_matchers, full)
    @file_matchers = file_matchers

    @full = full

    # keep track of how many substitutions for all file types
    @substitutions = Hash.new()
    file_matchers.each do |matcher|
      @substitutions[matcher] = 0
    end

    # keep track of missing tags, only in file types that have missing tags
    @missing_tags =  Hash.new(0)

    # keep track of how many files are copied
    @file_count = 0
    
    #keep track of how many directories are copied
    @dir_count = 0
  end

  def enter_without_mods(path)
    @dir_count += 1
    puts "Entering, no mods: #{path}" if @full
  end

  def enter_with_mods(path)
    @dir_count += 1
    puts "Entering, with mods: #{path}" if @full
  end

  def record_copy_without_mods(filename)
    @file_count += 1
    puts "    Copying, without mods: #{filename}" if @full
  end

  def record_copy_with_mods(filename)
    @file_count += 1
    puts "    Copying, with mods: #{filename}" if @full
  end

  def record_substitution(filename)
    puts "    Substituted license text into #{filename}" if @full
    matcher = which_match(filename)
    @substitutions[matcher] += 1
  end

  def record_missing_tag(filename, source_path)
    puts "WARN: Found no license tag in #{source_path}"
    matcher = which_match(filename)
    @missing_tags[matcher] += 1
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

  # Prepare the license-applicable directories as absolute paths.
  #
  def prepare_license_dir_paths(source_dir, license_dirs)
    paths = []
    license_dirs.each do |dir|
      paths << "#{source_dir}/#{dir}".gsub('//', '/')
    end
    return paths
  end

  # Does this filename match any of the patterns?
  #
  def filename_matches_pattern?(filename)
    @file_matchers.each do |pattern|
      return true if File.fnmatch(pattern, filename)
    end
    return false
  end

  # Recursively copy this directory, without adding license mods to any files,
  # unless we hit one of the licensed directories.
  #
  def copy_dir_without_mods(source_dir, target_dir)
    @stats.enter_without_mods(source_dir)
    Dir.mkdir(target_dir)
    Dir.foreach(source_dir) do |filename|
      source_path = "#{source_dir}/#{filename}"
      target_path = "#{target_dir}/#{filename}"

      if filename == '.'
      elsif filename == '..'
      elsif @license_dir_paths.include?(source_path)
        copy_dir_with_mods(source_path, target_path)
      elsif File.directory?(source_path)
        copy_dir_without_mods(source_path, target_path)
      else
        copy_file_without_mods(source_dir, target_dir, filename)
      end
    end
  end

  # Recursively copy this directory, adding license mods to any suitable files.
  #
  def copy_dir_with_mods(source_dir, target_dir)
    @stats.enter_with_mods(source_dir)

    Dir.mkdir(target_dir)
    Dir.foreach(source_dir) do |filename|
      source_path = "#{source_dir}/#{filename}"
      target_path = "#{target_dir}/#{filename}"

      if filename == '.'
      elsif filename == '..'
      elsif File.directory?(source_path)
        copy_dir_with_mods(source_path, target_path)
      elsif filename_matches_pattern?(filename)
        copy_file_with_mods(source_dir, target_dir, filename)
      else
        copy_file_without_mods(source_dir, target_dir, filename)
      end
    end
  end

  # This file either is not in a licensed directory, or doesn't match any of the
  # file-matching strings
  #
  def copy_file_without_mods(source_dir, target_dir, filename)
    @stats.record_copy_without_mods(filename)
    source_path = "#{source_dir}/#{filename}"
    target_path = "#{target_dir}/#{filename}"
    FileUtils.cp(source_path, target_path)
  end

  # This file is in a licensed directory, and matches at least one of the
  # file-matching strings. Replace the magic string with the license text.
  #
  def copy_file_with_mods(source_dir, target_dir, filename)
    @stats.record_copy_with_mods(filename)
    source_path = "#{source_dir}/#{filename}"
    target_path = "#{target_dir}/#{filename}"
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
      @stats.record_missing_tag(filename, source_path)
    elsif found == 1
      @stats.record_substitution(filename)
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

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  # Setup and get ready to process.
  # * source_dir is a String -- the path to the top level directory to be copied
  # * target_dir is a String -- the path to the top level directory to copy into
  #      (must not already exist!)
  # * license_dirs is an array of Strings -- relative paths to the directories that
  #      require license mods.
  # * file_matchers
  # * license_file is a String -- the path to the text of the license agreement
  #      (with a ${year} token in it)
  # * full_report is a Boolean -- if true, we give a full log instead of just a summary.
  #
  def initialize(source_dir, target_dir, license_dirs, file_matchers, license_file, full_report)
    if !File.exist?(source_dir)
      raise "Source directory does not exist: #{source_dir}"
    end

    if File.exist?(target_dir)
      raise "Target directory already exists: #{target_dir}"
    end

    if !File.exist?(license_file)
      raise "Source directory does not exist: #{license_file}"
    end

    @source_dir = source_dir
    @target_dir = target_dir
    @file_matchers = file_matchers

    @license_dirs = license_dirs
    @license_dir_paths = prepare_license_dir_paths(source_dir, license_dirs)

    @license_file = license_file
    @license_text = prepare_license_text(license_file)

    @full_report = full_report
    @stats = LicenserStats.new(file_matchers, full_report)
  end

  # Start the recursive copying.
  def process()
    copy_dir_without_mods(@source_dir, @target_dir)
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
    puts 'Missing tags'
    @stats.missing_tags.sort.each do |line|
      printf("%5d %s\n", line[1], line[0])
    end
    puts
    puts 'parameters:'
    puts "    source_dir = #{@source_dir}"
    puts "    target_dir = #{@target_dir}"
    puts "    license_dirs = #{@license_dirs.join(', ')}"
    puts "    file_matchers = #{@file_matchers.join(', ')}"
    puts "    license_file = #{@license_file}"
    puts "    full_report = #{@full_report}"
  end
end

# ------------------------------------------------------------------------
# BOGUS test harness
# ------------------------------------------------------------------------

=begin
source_dir = '/Vivoweb_Stuff/Testing_licenser/sourceDir'
target_dir = '/Vivoweb_Stuff/Testing_licenser/targetDir'

license_dirs = []
license_dirs << '/licensed'

license_file = '../doc/license.txt'
=end

#=begin
source_dir = '/Vivoweb_Stuff/Testing_licenser/trunk'
target_dir = '/Vivoweb_Stuff/Testing_licenser/distribution'

license_dirs = []
license_dirs << '/themes'
license_dirs << '/vitro-core/webapp'
license_dirs << '/vitro-core/services'

file_matchers = []
file_matchers << '*.java'
file_matchers << '*.jsp'
file_matchers << '*.tld'
file_matchers << '*.xsl'
file_matchers << '*.xslt'
file_matchers << '*.css'
file_matchers << '*.js'
file_matchers << 'build.xml'

license_file = '/Vivoweb_Stuff/Testing_licenser/trunk/doc/license.txt'
#=end

l = Licenser.new(source_dir, target_dir, license_dirs, file_matchers, license_file, false)
l.process
l.report
