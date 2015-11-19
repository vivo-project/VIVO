#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------

A utility that reads an RDF file, builds a model, sorts all of the URIs that have
labels, and associates those URIs with the labels in a supplied text file, one line
per label. 

These labels are assigned the language specified on the command line, and the 
resulting RDF statements are sent to standard output as N3.

On the command line provide the path to the RDF file, the path to the labels file,
and the desired language/locale. E.g.:

	label_inserter.rb ../../vivo-core-1.5-annotations.rdf labels.file es_ES

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require 'rubygems'
require 'rdf'
require 'label_common'

include RDF

class LabelInserter
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def sanity_check_arguments(args)
    raise UsageError, "usage is: label_inserter.rb <rdf_file> <labels_input_file> <locale> [filter_file] <n3_output_file> [ok]" unless (3..5).include?(args.length)

   	if args[-1].downcase == 'ok'
	  ok = true
	  args.pop
	else
	  ok = false
    end

    n3_output_file = args.pop
    raise UsageError, "File '#{n3_output_file}' already exists. specify 'ok' to overwrite it." if File.exist?(n3_output_file) && !ok

    rdf_file = args[0]
    raise UsageError, "File '#{rdf_file}' does not exist." unless File.exist?(rdf_file)

    labels_input_file = args[1]
    raise UsageError, "File '#{labels_input_file}' does not exist." unless File.exist?(labels_input_file)

    locale = args[2]
    raise UsageError, "Locale should be like 'ab' or 'ab-CD'." unless /^[a-z]{2}(-[A-Z]{2})?$/ =~ locale

    filter_file = args[3]
    raise UsageError, "File '#{filter_file}' does not exist." if filter_file && !File.exist?(filter_file)
    filter = LabelCommon.load_filter(filter_file)

    return rdf_file, labels_input_file, locale, filter, n3_output_file
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @rdf_file, @labels_input_file, @locale, @filter, @n3_output_file = sanity_check_arguments(args)
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

    labels = IO.readlines(@labels_input_file)
    
    raise "Number of labels (#{labels.length}) doesn't match number of URIs (#{solutions.length})" unless labels.length == solutions.length

	graph = Graph.new
   	solutions.zip(labels).each do |data|
   		s = data[0].prop
   		p = RDFS.label
   		o = Literal.new(data[1].chomp, :language => @locale)
   		graph << Statement.new(s, p, o)
   	end

    File.open(@n3_output_file, 'w') do |f|
    	f.puts graph.dump(:ntriples)
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
  inserter = LabelInserter.new(ARGV)
  inserter.process() 
end
