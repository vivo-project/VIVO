#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------

A utility that reads an RDF file, builds a model, sorts all of the URIs that have
labels, and produces a file of those labels, one per line. The idea is that this 
file could be translated, and the result could be put into RDF by label_inserter.rb

This required the RDF.rb gem: sudo gem install rdf

On the command line provide the path to the RDF file. E.g.:

	label_stripper.rb '../../vivo-core-1.5-annotations.rdf'

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require 'rubygems'
require 'rdf'
require 'label_common'
require 'rdf/n3'

include RDF

class LabelStripper
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def sanity_check_arguments(args)
    raise UsageError, "usage is: label_stripper.rb <rdf_file> [filter_file] <labels_output_file> [ok]" unless (2..3).include?(args.length)

  	if args[-1].downcase == 'ok'
	  ok = true
	  args.pop
	else
	  ok = false
    end
      
    output_file = args.pop
    raise UsageError, "File '#{output_file}' already exists. specify 'ok' to overwrite it." if File.exist?(output_file) && !ok
    
    rdf_file = args[0]
    raise UsageError, "File '#{rdf_file}' does not exist." unless File.exist?(rdf_file)

    filter_file = args[1]
    raise UsageError, "File '#{filter_file}' does not exist." if filter_file && !File.exist?(filter_file)
    filter = LabelCommon.load_filter(filter_file)

    return rdf_file, filter, output_file
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @rdf_file, @filter, @labels_output_file = sanity_check_arguments(args)
  rescue UsageError => e
    puts "\n----------------\nUsage error\n----------------\n\n#{e}\n\n----------------\n\n"
    exit
  rescue FilterError => e
    puts "\n----------------\nFilter file is invalid\n----------------\n\n#{e}\n\n----------------\n\n"
    exit
  end
  
  def process()
    query = Query.new({
      :prop => {
        RDFS.label => :label,
        }
      })

    solutions = LabelCommon.new(@rdf_file).process(query, &@filter)
    
    File.open(@labels_output_file, 'w') do |f|
      solutions.each do |s|
        f.puts s.label
      end
    end
  end
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
  stripper = LabelStripper.new(ARGV)
  stripper.process() 
end
