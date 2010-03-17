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

class OutputSummaryFormatter
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Write the beginning of the summary file.
  # This includes the HTML header, and the banner.
  #
  # f -- a file, already open for output.
  #
  def write_summary_header(f)
    if @osp.overall_status == Status::BAD
      status = "FAILURE"
      html_class = Status::html_class(Status::BAD)
    else
      status = "SUCCESS"
      html_class = Status::html_class(Status::GOOD)
    end

    f.print <<END_HEADER
<html>
<head>
  <title>Summary of Acceptance Tests #{@osp.start_time}</title>
  <link rel="stylesheet" type="text/css" href="summary.css">
</head>
<body>
  
  <div class="heading">
    Acceptance test results: #{@osp.start_time}
    <div class="#{html_class} one-word">#{status}</div>
  </div>
  
END_HEADER
  end

  # Write the first section of the summary file. This section contains
  # nested tables with fixed formats, containing overall stats.
  #
  # f -- a file, already open for output.
  #
  def write_summary_stats_section(f)
    how_many_tests = @osp.tests.length

    how_many_pass = 0
    how_many_fail = 0
    how_many_ignore = 0
    @osp.tests.each do |t|
      how_many_pass += 1 if t.status == Status::GOOD
      how_many_ignore += 1 if t.status == Status::FAIR
      how_many_fail += 1 if t.status == Status::BAD
    end

    if how_many_pass > 0
      pass_class = Status::html_class(Status::GOOD)
    else
      pass_class = ''
    end

    if how_many_fail > 0
      fail_class = Status::html_class(Status::BAD)
    else
      fail_class = ''
    end

    if how_many_ignore > 0
      ignore_class = Status::html_class(Status::FAIR)
    else
      ignore_class = ''
    end

    f.print <<END_STATS
  <div class="section">Summary</div>
  
  <table class="summary" cellspacing="0">
    <tr>
      <td>
        <table cellspacing="0">
          <tr><td>Start time:</td><td>#{@osp.start_time}</td></tr>
          <tr><td>End time</td><td>#{@osp.end_time}</td></tr>
          <tr><td>Elapsed time</td><td>#{@osp.elapsed_time}</td></tr>
        </table>
      </td>
      <td>
        <table cellspacing="0">
          <tr><td>Suites</td><td>#{@osp.suites.length}</td></tr>
          <tr><td>Total tests</td><td>#{@osp.tests.length}</td></tr>
          <tr class="#{pass_class}"><td>Passing tests</td><td>#{how_many_pass}</td></tr>
          <tr class="#{fail_class}"><td>Failing tests</td><td>#{how_many_fail}</td></tr>
          <tr class="#{ignore_class}"><td>Ignored tests</td><td>#{how_many_ignore}</td></tr>
        </table>
      </td>
    </tr>
  </table>

END_STATS
  end

  # Write a table of failed tests to the summary file, with links
  # to the detailed output for each test.
  #
  # While we're at it, write the list of failed tests to the console.
  #
  # f -- a file, already open for output.
  #
  def write_summary_failure_section(f)
    fails = []
    @osp.tests.each do |t|
      fails << t if t.status == Status::BAD
    end

    f.print "  <div class=section>Failing tests</div>\n\n  <table cellspacing=\"0\">\n"
    f.print "    <tr><th>Suite name</th><th>Test name</th></tr>\n"

    if fails.empty?
      f.print "    <tr>\n"
      f.print "      <td colspan=\"2\">No tests failed.</td>\n"
      f.print "    </tr>\n"
    else
      fails.each do |t|
        f.print "    <tr class=\"#{Status::html_class(t.status)}\">\n"
        f.print "      <td>#{t.suite_name}</td>\n"
        f.print "      <td><a href=\"#{t.output_link}\">#{t.test_name}</a></td>\n"
        f.print "    </tr>\n"

        puts ">>>>>TEST FAILED: #{t.suite_name}, #{t.test_name} (not in ignored_tests.txt)"
      end
    end

    f.print "    </table>\n\n"
  end

  # Write a table of ignored tests to the summary file, with links
  # to the detailed output for each test.
  #
  # f -- a file, already open for output.
  #
  def write_summary_ignore_section(f)
    ignores = []
    @osp.tests.each do |t|
      ignores << t if t.status == Status::FAIR
    end

    f.print "  <div class=section>Ignored tests</div>\n\n  <table cellspacing=\"0\">\n"
    f.print "    <tr><th>Suite name</th><th>Test name</th><th>Reason for ignoring</th></tr>\n"

    if ignores.empty?
      f.print "    <tr>\n"
      f.print "      <td colspan=\"3\">No tests ignored.</td>\n"
      f.print "    </tr>\n"
    else
      ignores.each do |t|
        f.print "    <tr class=\"#{Status::html_class(t.status)}\">\n"
        f.print "      <td>#{t.suite_name}</td>\n"
        f.print "      <td><a href=\"#{t.output_link}\">#{t.test_name}</a></td>\n"
        f.print "      <td>#{t.reason_for_ignoring}</td>\n"
        f.print "    </tr>\n"
      end
    end

    f.print "    </table>\n\n"
  end

  # Write a table of any error messages or warnings found in the log file.
  #
  # f -- a file, already open for output.
  #
  def write_error_messages_section(f)
    f.print "  <div class=section>Errors and warnings</div>\n"
    f.print "    <table cellspacing=\"0\">"

    if @osp.errors.empty? && @osp.warnings.empty?
      f.print "      <tr><td colspan=\"2\">No errors or warnings</td></tr>"
    else
      @osp.errors.each() do |e|
        f.print "      <tr class=\"bad\"><td>ERROR</td><td>#{e}</td></tr>"
      end
      @osp.warnings.each() do |w|
        f.print "      <tr class=\"fair\"><td>WARN</td><td>#{w}</td></tr>"
      end
    end
    f.print "    </table>\n\n"
  end

  # Write a table of the suites to the summary file, with links
  # to the detailed output for each suite.
  #
  # f -- a file, already open for output.
  #
  def write_summary_suites_section(f)
    f.print "  <div class=section>Suites</div>\n"
    f.print "    <table cellspacing=\"0\">\n"

    @osp.suites.each() do |s|
      f.print "      <tr class=\"#{Status::html_class(s.status)}\">\n"
      f.print "        <td><a href=\"#{s.output_link}\">#{s.name}</a></td>\n"
      f.print "      </tr>\n"
    end
    f.print "    </table>\n\n"
  end

  # Write a table of all tests to the summary file, with links
  # to the detailed output for each test.
  #
  # f -- a file, already open for output.
  #
  def write_summary_all_tests_section(f)
    f.print "  <div class=section>All tests</div>\n\n  <table cellspacing=\"0\">\n"
    f.print "    <tr><th>Suite name</th><th>Test name</th></tr>\n"

    if @osp.tests.empty?
      f.print "    <tr>\n"
      f.print "      <td colspan=\"2\">No tests.</td>\n"
      f.print "    </tr>\n"
    else
      @osp.tests.each do |t|
        f.print "    <tr class=\"#{Status::html_class(t.status)}\">\n"
        f.print "      <td>#{t.suite_name}</td>\n"
        f.print "      <td><a href=\"#{t.output_link}\">#{t.test_name}</a></td>\n"
        f.print "    </tr>\n"
      end
    end

    f.print "    </table>\n\n"
  end

  # Copy the log to the summary file, and close the HTML tags.
  #
  # f -- a file, already open for output.
  #
  def write_summary_footer(f)
    f.print "  <div class=section>Log</div>\n  <pre>\n"
    File.open(@log_file) do |log|
      FileUtils::copy_stream(log, f)
    end
    f.print "  </pre>\n</body>\n</html>\n\n"
  end

  # ------------------------------------------------------------------------------------
  public

  # ------------------------------------------------------------------------------------
  # Set up and get ready to process.
  #
  def initialize(output_suite_parser, log_file)
    @osp = output_suite_parser
    @log_file = log_file
  end

  def format(f)
    write_summary_header(f)
    write_summary_stats_section(f)
    write_error_messages_section(f)
    write_summary_failure_section(f)
    write_summary_ignore_section(f)
    write_summary_suites_section(f)
    write_summary_all_tests_section(f)
    write_summary_footer(f)
  end
end
