#! /usr/bin/ruby

require "#{File.dirname(__FILE__)}/subscripts/loadParms"
require "#{File.dirname(__FILE__)}/subscripts/test_result_merger"

properties  = {}

source_dir = "/home/jeb228/LoadTesting/tests/results/#{@version_name}"
properties["source_directory"] = source_dir
properties["target_directory"] = "/var/www/html/loadTesting/"
properties["version_name"] = ""#{@version_name}"

suggestions = []
if File.directory?(source_dir)
  Dir.chdir(source_dir) do |dir|
    if File.file?("fileOrderSuggestions.txt")
      File.open("fileOrderSuggestions.txt") do |f|
        f.each_line() do |line|
          suggestions.push(line.strip())
        end
      end
    end
  end
end
properties["file_order_suggestions"] = suggestions

trm = TestResultMerger.new(properties)
trm.merge

puts "TestResultMerger was successful."

