#! /usr/bin/ruby

=begin
--------------------------------------------------------------------------------

Parse a file of JMeter test results (*./jtl), summarize the times for each test,
and make the summaries easily available.

--------------------------------------------------------------------------------
=end

# ------------------------------------------------------------------------------------
# TestResultMarshaller class
# ------------------------------------------------------------------------------------

class TestResultMarshaller
  def marshall()
    File.open(@output_filename, 'w') do | out |
      write_html_header(out)
      write_table_header(out)
      write_table_lines(out)
      write_table_footer(out)
      write_html_footer(out)
    end
  end

  def write_html_header(out)
    out.puts <<"EOF"
<html>
<head>
  <link REL='STYLESHEET' TYPE='text/css' HREF='./mergedResults.css'>
  <title>Performance tests for #{@site_name}</title>
</head>
<body>      
EOF
  end

  def write_table_header(out)
    top_cells = ['<th>&nbsp</th>']
    @test_results.each do | test |
      top_cells.push("<th colspan='3'>#{test.version}<br/>#{test.filename}<br/>#{test.timestamp.strftime('%Y-%m-%d %H:%M:%S')}</th>")
    end

    bottom_cells = ['<th>Test Name</th>']
    @test_results.each do | test |
      bottom_cells.push('<th>Iterations</th>')
      bottom_cells.push('<th>time<br/>(min/max)</th>')
      bottom_cells.push("<th>compare</th>")
    end

    out.puts <<"EOF"
<table class='testData' cellspacing='0'>
  <tr>
    #{top_cells.join("\n    ")}
  </tr>
  <tr>
    #{bottom_cells.join("\n    ")}
  </tr>
EOF
  end

  def write_table_lines(out)
    all_test_names().each do | test_name |
      out.puts <<"EOF"
    <tr>
      <td class='left'>#{test_name}</td>
      #{format_test_results(test_name)}
    </tr>
EOF
    end
  end

  def all_test_names
    names = []
    @test_results.each do | test |
      names.concat(test.summaries.keys)
    end
    names.uniq.sort
  end

  def format_test_results(test_name)
    results = []
    @test_results.each do | test |
      results.push(format_test_result(test_name, test))
    end
    results.join("\n    ")
  end

  def format_test_result(test_name, test)
    s = test.summaries[test_name]
    if s
      <<"EOF"
    <td class='open'>#{s.how_many}</td>
    <td>
      <table class='oneResult close' cellspacing='0'>
        <tr>
          <td rowspan='2'>#{format_millis(s.avg_time)}</td>
          <td class='minmax'>#{format_millis(s.min_time)}</td>
        </tr>
        <tr>
          <td class='minmax'>#{format_millis(s.max_time)}</td>
        </tr>
      </table>
    </td>
    <td>#{performance_ratio(test_name, s.avg_time)}</td>
EOF
    else
      <<"EOF"
    <td class='open'>&nbsp;</td>
    <td>
      <table class='oneResult close' cellspacing='0'>
        <tr>
          <td rowspan='2'>&nbsp;</td>
          <td class='minmax'>&nbsp;</td>
        </tr>
        <tr>
          <td class='minmax'>&nbsp;</td>
        </tr>
      </table>
    </td>
    <td>&nbsp;</td>
EOF
    end
  end

  def format_millis(millis)
    "%.3f" % [millis.to_f / 1000]
  end
  
  def performance_ratio(test_name, time)
    return "&nbsp;" if @test_results.empty?
    return "&nbsp;" unless @test_results[0].summaries.key?(test_name)

    s = @test_results[0].summaries[test_name]
    reference = s.avg_time
    return "&nbsp;" if reference == 0

    return "#{"%.0f" % [time * 100 / reference]}%"    
  end

  def write_table_footer(out)
    out.puts "</table>"
  end

  def write_html_footer(out)
    out.puts <<"EOF"
</body>
</html>
EOF
  end

  def initialize(target_directory, site_name, test_results)
    @target_directory = target_directory
    @site_name = site_name
    @test_results = test_results

    filename = "#{site_name}-merged_#{Time.now.strftime('%Y-%m-%d_%H-%M-%S')}"
    @output_filename = File.expand_path(filename, @target_directory)
  end
end

