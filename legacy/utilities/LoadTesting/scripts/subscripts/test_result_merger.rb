#! /usr/bin/ruby

=begin
--------------------------------------------------------------------------------

Look through a directory of test results files (*.jtl), and produce an HTML file
that summarizes, merges, and compares the information.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end
$: << File.dirname(File.expand_path(__FILE__))
require 'test_result_file'
require 'test_result_marshaller'

# ------------------------------------------------------------------------------------
# TestResultMerger class
# ------------------------------------------------------------------------------------

class TestResultMerger
  #
  # Do we have any chance of succeeding with these properties?
  #
  def sanity_checks_on_properties()
    raise("Properties must contain a value for 'source_directory'") if @source_directory == nil
    raise("Properties must contain a value for 'target_directory'") if @target_directory == nil
    raise("Properties must contain a value for 'site_name'") if @site_name == nil

    if !File.directory?(@source_directory)
      raise "Not a directory: '#{@source_directory}'."
    end
    if !File.directory?(@target_directory)
      raise "Not a directory: '#{@target_directory}'."
    end
  end

  def parse_files()
    test_result_files = build_file_list()

    @test_results = []
    test_result_files.each() do | test_result_file |
      puts "Parsing #{test_result_file}"
      @test_results.push(TestResultFile.new(test_result_file, @source_directory))
    end
  end

  def build_file_list
    existing_files = []
    Dir.foreach(@source_directory) do | filename |
      next unless File.extname(filename) == ".jtl"
      existing_files.push(File.basename(filename, ".jtl"))
    end
    puts "BOGUS existing files = [#{existing_files.join(', ')}]"

    file_list = []
    @file_order_suggestions.each() do | suggestion|
      if existing_files.include?(suggestion)
        file_list.push(suggestion)
        existing_files.delete(suggestion)
      end
    end
    file_list.concat(existing_files)
    puts "BOGUS file list = [#{file_list.join(', ')}]"
    
    return file_list
  end
  
  def marshall()
    marshaller = TestResultMarshaller.new(@target_directory, @site_name, @test_results)
    marshaller.marshall()
  end

  def initialize(properties)
    @source_directory = properties['source_directory']
    @target_directory = properties['target_directory']
    @site_name = properties['site_name']
    @file_order_suggestions = properties['file_order_suggestions']

    puts "source_directory = #{@source_directory}"
    puts "target_directory = #{@target_directory}"
    puts "site_name = #{@site_name}"
    if (@file_order_suggestions == nil)
      puts "file_order_suggestions = nil"
    else
      puts "file_order_suggestions = [#{@file_order_suggestions.join(', ')}]"
    end

    sanity_checks_on_properties
  end

  def merge()
    parse_files()
    marshall()
  end
end

