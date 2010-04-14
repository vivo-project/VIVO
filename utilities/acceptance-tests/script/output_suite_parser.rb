=begin
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------

Parameters:

--------------------------------------------------------------------------------
=end
require 'date'
# This one is needed to get Time.parse() ????? Go figure.
require 'open-uri'

class OutputSuiteParser
  attr :errors
  attr :warnings

  attr :start_time
  attr :end_time
  attr :elapsed_time

  attr :suites
  attr :tests

  attr :overall_status

  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Scan the log file for start time, end time, and the names of the suites that
  # were run. Figure elapsed time also.
  #
  def parse_log_file()
    @start_time = nil
    @end_time = nil
    @elapsed_time = "unknown"

    @suite_names = []

    File.open(@log_file) do |f|
      f.each_line do |line|
        md = %r{Start time: (.*)$}.match line
        if md
          @start_time = md[1]
        end

        md = %r{End time: (.*)$}.match line
        if md
          @end_time = md[1]
        end

        md = %r{Running suite (.*)$}.match line
        if md
          @suite_names << md[1]
        end

        md = %r{ERROR\s*(.*)}.match line
        if md
          @errors << md[1]
        end

        md = %r{WARN\s*(.*)}.match line
        if md
          @warnings << md[1]
        end
      end
    end

    if @start_time && @end_time
      @elapsed_time = format_elapsed_time(@start_time, @end_time)
    end
  end

  # Scan the output of a suite run.
  #
  def parse_suite_output(suite_name)
    file_name = @output_manager.output_filename(suite_name)

    s = SuiteInfo.new
    s.name = suite_name
    s.output_link = File.basename(file_name)
    s.status = Status::GOOD
    @suites << s

    tests = []
    begin
      File.open(file_name) do |f|
        f.each_line do |line|
          md = %r{<tr class="\s*(\w+)"><td><a href="(#\w+)">([^<]*)</a></td></tr>}.match line
          if md
            t = TestInfo.new
            t.test_name = md[3]
            t.suite_name = s.name
            t.output_link = s.output_link + md[2]
            if md[1] == 'status_passed'
              t.status = Status::GOOD
            elsif @output_manager.ignore_test?(t.suite_name, t.test_name)
              t.status = Status::FAIR
              t.reason_for_ignoring = @output_manager.get_reason_for_ignoring(t.suite_name, t.test_name)
            else
              t.status = Status::BAD
            end
            tests << t
          end
        end
      end
    rescue Exception
      log_error("Failed to parse output for suite '#{s.name}': #{$!}")
      s.status = Status::BAD
    end

    s.status = get_worst_status(s.status, tests)
    @tests = @tests.concat(tests)
  end

  # Look at the info from all of the suites and prepare summary info.
  #
  def collate_and_summarize()
    status = Status::GOOD
    status = Status::FAIR if !@warnings.empty?
    status = Status::BAD if !@errors.empty?
    @overall_status = get_worst_status(status, @suites)
  end

  # Find the worst status in an array of tests or suites
  #
  def get_worst_status(starting_status, statused)
    worst = starting_status
    statused.each do |s|
      worst = s.status if s.status > worst
    end
    return worst
  end

  # Take two parsable time stamps and express the elapsed time as a string.
  # Examples: 4h 37m 6.7s
  #           15m 4s
  #           55.6s
  #
  def format_elapsed_time(start_time_string, end_time_string)
    start = Time.parse(start_time_string)
    ender = Time.parse(end_time_string)
    elapsed = ender - start
    s = elapsed % 60
    m = ((elapsed - s) / 60) % 60
    h = (elapsed - s - (60 * m))/ 3600
    elapsed_time = ""
    elapsed_time << "#{h.to_i}h " if h > 0
    elapsed_time << "#{m.to_i}m " if h > 0 || m > 0
    elapsed_time << "#{s}s"
  end

  def log_error(message)
    @output_manager.log("ERROR", message)
    # By the time we get here, we've already scanned the log file for errors.
    @errors << message
  end

  # ------------------------------------------------------------------------------------
  public

  # ------------------------------------------------------------------------------------
  #
  # Set up and get ready to process.
  #
  def initialize(output_manager, log_file)
    @output_manager = output_manager
    @log_file = log_file

    @errors = []
    @warnings = []
    @suites = []
    @tests = []
  end

  # Parse the output from each suite, and the log file, and munge it all together
  # for easy access.
  #
  def parse()
    parse_log_file()
    @suite_names.each do |suite_name|
      parse_suite_output(suite_name)
    end
    collate_and_summarize()
  end
end
