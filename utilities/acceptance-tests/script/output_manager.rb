=begin
--------------------------------------------------------------------------------

Stop the Vitro application, delete all MySQL tables from the Vitro database, and
start the application again.

--------------------------------------------------------------------------------

Parameters:
  tomcat_stop_command
    A "shell" command that will stop the Tomcat server.
  tomcat_stop_delay
    Number of seconds to wait after the tomcat_stop_command returns before
    proceeding.
  tomcat_start_command
    A "shell" command that will start the Tomcat server.
  tomcat_start_delay
    Number of seconds to wait after the tomcat_start_command returns before
    proceeding.
  mysql_username
    A user account that has authority to drop the Vitro database in MySQL.
  mysql_password
    The password for mysql_username.
  database_name
    The name of the Vitro database in MySQL.

--------------------------------------------------------------------------------
=end
require 'date'
require 'fileutils'
require File.expand_path('output_suite_parser', File.dirname(File.expand_path(__FILE__)))
require File.expand_path('output_summary_formatter', File.dirname(File.expand_path(__FILE__)))

class TestInfo
  attr :test_name, true
  attr :suite_name, true
  attr :output_link, true
  attr :status, true
end

class SuiteInfo
  attr :name, true
  attr :output_link, true
  attr :status, true
end

class Status
  GOOD = 0
  FAIR = 1
  BAD = 2
  def self.html_class(status)
    return %w{good fair bad}[status]
  end
end

class OutputManager
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Confirm that the output directory parameter is reasonable.
  #
  def sanity_checks_on_parameters()
    if @output_directory == nil
      raise("Properties file must contain a value for 'output_directory'")
    end

    if !File.exist?(@output_directory)
      raise "Output directory  '#{@output_directory}' does not exist."
    end

    if !File.readable?(@output_directory)
      raise "Output directory  '#{@output_directory}' is not readable."
    end

    if !File.directory?(@output_directory)
      raise "Output directory  '#{@output_directory}' is not a directory."
    end

    if !File.writable?(@output_directory)
      raise "Output directory '#{@output_directory}' is not writable."
    end

    if @ignored_tests_file == nil
      raise("Properties file must contain a value for 'ignored_tests_file'")
    end

    if !File.exist?(@ignored_tests_file)
      raise "Ignored tests file  '#{@ignored_tests_file}' does not exist."
    end

    if !File.readable?(@ignored_tests_file)
      raise "Ignored tests file  '#{@ignored_tests_file}' is not readable."
    end

    if !File.file?(@ignored_tests_file)
      raise "Ignored tests file  '#{@ignored_tests_file}' is not a file."
    end
  end

  # Load the list of ignored tests. Each line is [suite_name], [test_name]
  #
  def load_list_of_ignored_tests()
    ignored_tests = []
    File.open(@ignored_tests_file) do |f|
      f.each_line do |line|
        line.strip!
        if line.length == 0 || line[0] == ?# || line[0] == ?!
          # ignore blank lines, and lines starting with '#' or '!'.
        elsif line =~ /^([^,]+),([^,]+)$/
          # suite name and test name separated by ',' and optional whitespace.
          ignored_tests << [$1.strip, $2.strip]
        else
          raise "Invalid line in ignored tests file: '#{line}'"
        end
      end
    end
    return ignored_tests
  end

  # The CSS file for the output summary exists in the script directory.
  # Copy it to the output directory.
  #
  def copy_css_file()
    source = File.expand_path('output_summary.css', File.dirname(File.expand_path(__FILE__)))
    dest = File.expand_path('summary.css', @output_directory)
    FileUtils::copy_file(source, dest)
  end

  # ------------------------------------------------------------------------------------
  public

  # ------------------------------------------------------------------------------------
  # Set up and get ready to process.
  #
  def initialize(properties)
    @output_directory = properties['output_directory']
    @ignored_tests_file = properties['ignored_tests_file']

    sanity_checks_on_parameters()

    @log_file = File.expand_path("log_file.txt", @output_directory)
    FileUtils::remove_file(@log_file) if File.exist?(@log_file)

    @output_summary_file = File.expand_path("index.html", @output_directory)
    FileUtils::remove_file(@output_summary_file) if File.exist?(@output_summary_file)

    @ignored_tests = load_list_of_ignored_tests()
    puts ">>>ignored tests: #{@ignored_tests}"
  end

  # Write a message to the log file
  #
  def log(level, message)
    File.open(@log_file, File::CREAT | File::APPEND | File::WRONLY) do |f|
      f.print("#{level} #{message}\n")
    end
  end

  # Where can we find the output for this suite?
  #
  def output_filename(suite_name)
    File.expand_path("#{suite_name}_output.html", @output_directory)
  end

  # Have we decided to ignore this test if it fails?
  #
  def ignore_test?(suite_name, test_name)
    @ignored_tests.each do |pair|
      return true if pair[0] == suite_name && pair[1] == test_name
    end
    return false
  end

  def summarize()
    @osp = OutputSuiteParser.new(self, @log_file)
    @osp.parse()

    copy_css_file()
    File.open(@output_summary_file, File::CREAT | File::WRONLY) do |f|
      osf = OutputSummaryFormatter.new(@osp, @log_file)
      osf.format(f)
    end
  end
end