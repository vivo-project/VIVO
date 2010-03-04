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
require 'database_cleanser'

=begin
<h1>Test suite results </h1>
<table>
<tr><td>result:</td><td>passed</td></tr>
<tr><td>totalTime:</td><td>12</td></tr>
<tr><td>numTestTotal:</td><td>6</td></tr>
<tr><td>numTestPasses:</td><td>6</td></tr>
<tr><td>numTestFailures:</td><td>0</td></tr>
<tr><td>numCommandPasses:</td><td>71</td></tr>
<tr><td>numCommandFailures:</td><td>0</td></tr>
<tr><td>numCommandErrors:</td><td>0</td></tr>
<tr><td>Selenium Version:</td><td>2.0</td></tr>
<tr><td>Selenium Revision:</td><td>a1</td></tr>
<tr><td>
  <table id="suiteTable" class="selenium" border="1" cellpadding="1" cellspacing="1"><tbody>
  <tr class="title status_passed"><td><b>Test Suite</b></td></tr>
  <tr class="  status_passed"><td><a href="#testresult0">Create New User</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult1">First Time Login</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult2">Try New Password</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult3">Edit User Info</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult4">Confirm Edited Info</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult5">Delete New User</a></td></tr>
  </tbody></table>
  </td><td>&nbsp;</td></tr>
</table>
=end

class AcceptanceRunner
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Confirm that the parameters are reasonable. They point to valid files, directories,
  # and URL.
  #
  def sanity_checks_on_parameters()
    confirm_is_readable_directory(@test_root_directory, "Test root directory")
    if get_sub_directories(@test_root_directory).empty?
      raise "Test root directory '#{@test_root_directory}' has no sub-directories."
    end

    confirm_is_readable_directory(@output_directory, "Output directory")
    if !File.writable?(@output_directory)
      raise "Output directory '#{@output_directory}' is not writable."
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
      raise "#{label} '#{path}' is not a directory."
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
    get_sub_directories(@test_root_directory).each do |suite_path|
      cleanse_data_model()
      run_test_suite(suite_path)
    end
  end
  
  def cleanse_data_model()
    puts "BOGUS cleanse_data_model()"
  end
  
  def run_test_suite(suite_path)
    puts "BOGUS run_test_suite()"
  end

  def create_summary_html()
    puts "BOGUS create_summary_html()"
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  # Set up and get ready to process.
  #
  def initialize(website_url, test_root_directory, output_directory, user_extensions_path, firefox_profile_template_path)
    @website_url = website_url
    @test_root_directory = test_root_directory
    @output_directory = output_directory
    @user_extensions_path = user_extensions_path
    @firefox_profile_template_path = firefox_profile_template_path
    sanity_checks_on_parameters()
  end

  # Run all of the test suites and produce an output summary page.
  def run
    run_all_suites()
    create_summary_html()
  end
end

# ------------------------------------------------------------------------
# Main routine
# ------------------------------------------------------------------------

# BOGUS test harness
website_url = "http://localhost:8080/vivo/"
test_root_directory = "C:\\eclipseVitroWorkspace\\vivoweb\\utilities\\acceptance-tests\\suites"
output_directory = "C:\\eclipseVitroWorkspace\\vivoweb\\utilities\\acceptance-tests\\script\\output"
user_extensions_path = "C:\\eclipseVitroWorkspace\\vivoweb\\utilities\\acceptance-tests\\selenium\\user-extensions.js"
firefox_profile_template_path = "C:\\Vitro_stuff\\Selenium\\experiments\\profiles\\selenium"

ar = AcceptanceRunner.new(website_url, test_root_directory, output_directory, user_extensions_path, firefox_profile_template_path)
ar.run()