#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------

A utility that reads query times from a VIVO log file, and sums them based on a
list of partitioning expressions.

1) The file of partitioning expressions is parsed and stored.
2) The log file is scanned for records from the RDFServiceLogger, which are
   accumulated in the program storage.
3) The partitioning expressions are tested against each log record, and matches
   are recorded. 
      Strings of white space are treated as single spaces, both in the 
      partitioning expressions and in the log records. This makes the 
      expressions easier to read, both in the partitions file and in the program 
      output.
4) For each partioning expression, report:
    a) the expression
    b) how many queries matched it
    c) the total time spent in those queries
5) As errors, report:
    a) how many queries (and how much time) matched no expression. Dump a few of
       them to the error file.
    b) how many queries (and how much time) matched multiple expressions. Dump a
       few of them to the error file.

--------------------------------------------------------------------------------

Command line: scan_query_times.rb [partition_file] [log_file] [overlap_file] [unmatched_file]
   If the wrong number of arguments, abort.
   If either file does not exist, abort.

--------------------------------------------------------------------------------

Partitioning expressions: parse the lines in the file
   If a line is empty, ignore it.
   If a line begins with a #, ignore it.
   If a line begins with the word "STACK ", then the remainder of the line
      (trimmed) is a regular expression to be matched against the stack trace of
      each log record.
   If a line begins with the word "QUERY ", then the remainder of the line
      (trimmed) is a regular expression to be matched against the query text of
      each log record.
   If a line begins with the word "GROUP ", then the remainder of the line
      (trimmed) is the text name of the group. All matchers that follow are
      included in that named group, for summary output purposes. Matcher found
      before the first GROUP line are in the unnamed group, as are any that
      follow a GROUP line with no name.
   Otherwise, abort.

--------------------------------------------------------------------------------

A log record begins like this:
   2014-04-29 14:26:00,798 INFO  [RDFServiceLogger]    0.001 sparqlAskQuery [ASK {...

   The timestamp is ignored.
   The log leve is ignored.
   The logging class must be "RDFServiceLogger"
   The time is recorded.
   The method name is ignored.
   The remainder of the text on that line (trimmed) is recorded as the query.
   Any subsequent lines are the stack trace.

The end of the log record is a line that does not begin with whitespace.

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))

require 'partition'
require 'scanner'

class UsageError < StandardError; end

#
# Parse the arguments and complain if they don't make sense.
#
def parse_args(args)
  raise UsageError, "usage is: scan_query_times.rb <partition_file> <log_file> <overlap_file> <unmatched_file>" unless (args.length == 4)

  partition_file = args[0]
  raise UsageError, "File '#{partition_file}' does not exist." unless File.exist?(partition_file)
  log_file = args[1]
  raise UsageError, "File '#{log_file}' does not exist." unless File.exist?(log_file)
  overlap_file = args[2]
  raise UsageError, "File '#{overlap_file}' does not exist." unless File.exist?(overlap_file)
  unmatched_file = args[3]
  raise UsageError, "File '#{unmatched_file}' does not exist." unless File.exist?(unmatched_file)

  return partition_file, log_file, overlap_file, unmatched_file
end

#
#
# ------------------------------------------------------------------------------------
# Standalone calling.
#
# Do this if this program was called from the command line. That is, if the command
# expands to the path of this file.
# ------------------------------------------------------------------------------------
#

if File.expand_path($0) == File.expand_path(__FILE__)
  begin
    partition_file, log_file, overlap_file, unmatched_file = parse_args(ARGV)
    partition = Partition.new(partition_file, overlap_file, unmatched_file)
    scanner = Scanner.new(partition)
    scanner.process(log_file)
    partition.report
  rescue UsageError => e
    puts "\n----------------\nUsage error\n----------------\n\n#{e}\n\n----------------\n\n"
    exit
  end
end
