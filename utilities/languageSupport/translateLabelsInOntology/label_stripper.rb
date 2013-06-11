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

include RDF

class LabelStripper
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def sanity_check_arguments(args)
    raise "usage is: label_stripper.rb <rdf_file> <labels_output_file> [ok]" unless (2..3).include?(args.length)

	if args[2].nil?
	  ok = false
	elsif args[2].downcase == 'ok'
	  ok = true
	else
	  raise "third argument, if present, must be 'ok'"
    end
        
    rdf_file = args[0]
    raise "File '#{rdf_file}' does not exist." unless File.exist?(rdf_file)

    labels_output_file = args[1]
    raise "File '#{labels_output_file}' already exists. specify 'ok' to overwrite it." if File.exist?(labels_output_file) && !ok

    return rdf_file, labels_output_file
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @rdf_file, @labels_output_file = sanity_check_arguments(args)
  end
  
  def process(&filter)
    filter = filter || lambda{true}
    
    query = Query.new({
      :prop => {
        RDFS.label => :label,
        }
      })

    solutions = LabelCommon.new(@rdf_file).process(query, &filter)
    
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

vivo_filter = lambda {|s| s.prop.start_with?("http://vivoweb.org/ontology/core#") && !s.label.to_s.strip.empty?}

if File.expand_path($0) == File.expand_path(__FILE__)
  stripper = LabelStripper.new(ARGV)
  stripper.process(&vivo_filter) 
end
