=begin
--------------------------------------------------------------------------------

Run the acceptance tests, and summarize their output.

--------------------------------------------------------------------------------

Parameters:
    -- the root directory for the tests. Probably something ending in
         vivoweb/utilities/acceptance-tests/suites
    -- the directory for the output.
    -- the path to the user extensions script.
    -- the directory of the Firefox profile template.
    -- the URL of the web site under test.

--------------------------------------------------------------------------------

What are we doing?
-- Test the parameters.
  -- Does each directory exist? Is it readable? writeable?
  -- Does the URL produce a response?
  -- Does user-extensions.js exist?
-- For each subdirectory in the suites folder:
  -- Run the reset process, whatever that is.
    -- Stop tomcat, reset MySql database, start tomcat
  -- Find the Suite.html
    -- If none, throw warning into the log and continue.
  -- Run the suite, sending the output to a file named after the subdirectory
    -- If failure, throw error into the log and continue.
-- Create a summary output file.
     result:  passed | failed         -- "and" of all suites.
     total time:   seconds            -- capture the time before and after.
     number of tests:                 -- sum from all suites.
     number of passing tests:         -- sum from all suites.
     number of failing tests:         -- sum from all suites.

     Table of links to the suite output files.
--------------------------------------------------------------------------------
=end
require 'open-uri'
require 'date'
require File.expand_path('database_cleanser', File.dirname(File.expand_path(__FILE__)))
require File.expand_path('upload_area_cleanser', File.dirname(File.expand_path(__FILE__)))
require File.expand_path('property_file_reader', File.dirname(File.expand_path(__FILE__)))
require File.expand_path('output_manager', File.dirname(File.expand_path(__FILE__)))

class AcceptanceRunner
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Confirm that the parameters are reasonable. They point to valid files, directories,
  # and URL.
  #
  def sanity_checks_on_parameters()
    raise("Properties file must contain a value for 'website_url'") if @website_url == nil
    raise("Properties file must contain a value for 'test_root_directory'") if @test_root_directory == nil
    raise("Properties file must contain a value for 'user_extensions_path'") if @user_extensions_path == nil
    raise("Properties file must contain a value for 'firefox_profile_template_path'") if @firefox_profile_template_path == nil
    raise("Properties file must contain a value for 'suite_timeout_limit'") if @suite_timeout_limit == nil
    raise("Properties file must contain a value for 'selenium_jar_path'") if @selenium_jar_path == nil

    confirm_is_readable_directory(@test_root_directory, "Test root directory")
    if get_sub_directories(@test_root_directory).empty?
      raise "Test root directory '#{@test_root_directory}' has no sub-directories."
    end

    if File.basename(@user_extensions_path) != "user-extensions.js"
      raise "User extensions file must be named 'user-extensions.js', not '#{File.basename(@user_extensions_path)}'"
    end
    confirm_is_readable_file(@user_extensions_path, "User extensions file")

    confirm_is_readable_directory(@firefox_profile_template_path, "Firefox profile template")

    begin
      open(@website_url) {|f|}
    rescue Exception
      raise "Unable to connect to web-site at '#{@website_url}'"
    end
  end

  # Does this path point to an existing, readable directory?
  #
  def confirm_is_readable_directory(path, label)
    confirm_is_readable(path, label)
    if !File.directory?(path)
      raise "#{label} '#{path}' is not a directory."
    end
  end

  # Does this path point to an existing, readable file?
  #
  def confirm_is_readable_file(path, label)
    confirm_is_readable(path, label)
    if !File.file?(path)
      raise "#{label} '#{path}' is not a file."
    end
  end

  # Does this path point to something that exists and is readable?
  #
  def confirm_is_readable(path, label)
    if !File.exist?(path)
      raise "#{label} '#{path}' does not exist."
    end
    if !File.readable?(path)
      raise "#{label} '#{path}' is not readable."
    end
  end

  # Get an array of paths to all of the sub-directories in this directory.
  #
  def get_sub_directories(directory_path)
    subs = []
    Dir.foreach(directory_path) do |filename|
      if filename[0,1] != '.'
        path = File.expand_path(filename, directory_path)
        subs << path if File.directory?(path)
      end
    end
    return subs
  end

  # For each directory under the test root, cleanse the data model and run
  # the test suite.
  #
  def run_all_suites()
    time_stamp("Start time")
    get_sub_directories(@test_root_directory).each do |suite_path|
      suite_file_path = File.expand_path("Suite.html", suite_path)
      if File.exist?(suite_file_path)
        cleanse_the_model()
        run_test_suite(suite_file_path)
      else
        log_warn("No suite file found in #{suite_path}")
      end
    end
    time_stamp("End time")
  end

  # Before each suite, call the cleansers.
  def cleanse_the_model()
    @database_cleanser.cleanse()
    @upload_area_cleanser.cleanse()
  end

  def run_test_suite(suite_file_path)
    suite_name = File.basename(File.dirname(suite_file_path))
    log_info("Running suite #{suite_name}")
    output_file = @output_manager.output_filename(suite_name)

    args = []
    args << "-jar" << @selenium_jar_path
    args << "-singleWindow"
    args << "-timeout" << @suite_timeout_limit.to_s
    args << "-userExtensions" << @user_extensions_path
    args << "-firefoxProfileTemplate" << @firefox_profile_template_path
    args << "-htmlSuite" << "*firefox" << @website_url << suite_file_path << output_file

    result = system("java", *args)
    raise("Can't find the 'java' command!") if result == nil
    if $?.exitstatus != 0
      log_warn("Suite failed: '#{suite_name}, return code was #{$?.exitstatus}")
    end
  end

  def time_stamp(label)
    log_info("#{label}: #{DateTime.now.strftime('%Y/%m/%d %H:%M:%S')}")
  end

  def log_info(message)
    @output_manager.log("INFO ", message)
  end

  def log_warn(message)
    @output_manager.log("WARN ", message)
  end

  def log_error(message)
    @output_manager.log("ERROR", message)
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  # Set up and get ready to process.
  #
  def initialize(properties)
    @website_url = properties['website_url']
    @test_root_directory = properties['test_root_directory']
    @user_extensions_path = properties['user_extensions_path']
    @firefox_profile_template_path = properties['firefox_profile_template_path']
    @suite_timeout_limit = properties['suite_timeout_limit'].to_i
    @selenium_jar_path = properties['selenium_jar_path']

    sanity_checks_on_parameters()

    @database_cleanser = DatabaseCleanser.new(properties)
    @upload_area_cleanser = UploadAreaCleanser.new(properties)
    
    @output_manager = OutputManager.new(properties)
    @output_manager.empty_log()
  end

  # Run all of the test suites
  def run
    run_all_suites()
    # To collate the output from the suites, use OutputManager.new(properties).summarize()
  end
end

#
#
# ------------------------------------------------------------------------------------
# Standalone calling.
# ------------------------------------------------------------------------------------
#
if ARGV.length == 0
  raise("No arguments - usage is: ruby run_acceptance_test.rb <properties_file>")
end
if !File.file?(ARGV[0])
  raise "File does not exist: '#{ARGV[0]}'."
end

properties = PropertyFileReader.read(ARGV[0])

ar = AcceptanceRunner.new(properties)
ar.run()
