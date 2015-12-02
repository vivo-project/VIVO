#! /usr/bin/ruby

=begin
--------------------------------------------------------------------------------

Parse a file of JMeter test results (*./jtl), summarize the times for each test,
and make the summaries easily available.

--------------------------------------------------------------------------------
=end

require "rexml/document"

include REXML

# ------------------------------------------------------------------------------------
# TestResultSummary class
# ------------------------------------------------------------------------------------

class TestResultSummary
  attr_reader :label
  attr_reader :how_many
  attr_reader :failures
  attr_reader :min_time
  attr_reader :max_time
  attr_reader :avg_time
  def addResult(result_element)
    @how_many += 1
    @failures += 1 unless result_element.attributes["s"] == "true"

    time = result_element.attributes["t"].to_i
    @total_time += time
    @min_time = [@min_time, time].min
    @max_time = [@max_time, time].max

    @avg_time = @total_time / how_many
  end

  def initialize(result_element)
    @label = result_element.attributes["lb"]
    @how_many = 0
    @failures = 0
    @min_time = 100000000
    @max_time = 0
    @total_time = 0

    addResult(result_element)
  end
end

# ------------------------------------------------------------------------------------
# TestResultFile class
# ------------------------------------------------------------------------------------

class TestResultFile
  attr_reader :filename
  attr_reader :timestamp
  attr_reader :summaries
  attr_reader :version
  def parse_result_file()
    @summaries = {}
    @version = "_"

    file = File.new( @file_path )
    doc = Document.new file
    XPath.each(doc, "/testResults/httpSample") do | result |
      test_label = result.attributes["lb"]
      if @summaries[test_label] == nil
        @summaries[test_label] = TestResultSummary.new(result)
      else
        @summaries[test_label].addResult(result)
      end
    end
    
    XPath.each(doc, "version") do | version |
      @version = version.attributes["name"]
    end
  end

  def initialize(filename, source_directory)
    raise("filename must not be nil") if filename == nil
    raise("source_directory must not be nil") if source_directory == nil

    @filename = filename
    @source_directory = source_directory

    if !File.directory?(@source_directory)
      raise "Directory does not exist: '#{@source_directory}'."
    end

    @file_path = File.expand_path(filename + ".jtl", @source_directory)

    if !File.file?(@file_path)
      raise "File doesn't exist: '#{@file_path}'."
    end

    @timestamp = File.mtime(@file_path)

    parse_result_file()
  end
end

