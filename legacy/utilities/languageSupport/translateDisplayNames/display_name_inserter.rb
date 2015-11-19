#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------

A utility that reads an RDF file, builds a model, sorts all of the URIs that have
display_names, and associates those URIs with the display_names in a supplied text file, one line
per display_name. 

These display_names are assigned the language specified on the command line, and the 
resulting RDF statements are sent to standard output as N3.

On the command line provide the path to the RDF file, the path to the display_names file,
and the desired language/locale. E.g.:

	display_name_inserter.rb ../../vivo-core-1.5-annotations.rdf display_names.file es_ES

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require 'rubygems'
require 'rdf'
require 'display_name_common'

include RDF

class DisplayNameInserter
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def sanity_check_arguments(args)
    raise UsageError, "usage is: display_name_inserter.rb <rdf_file> <display_names_input_file> <locale> [filter_file] <n3_output_file> [ok]" unless (3..5).include?(args.length)

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

    display_names_input_file = args[1]
    raise UsageError, "File '#{display_names_input_file}' does not exist." unless File.exist?(display_names_input_file)

    locale = args[2]
    raise UsageError, "Locale should be like 'ab' or 'ab-CD'." unless /^[a-z]{2}(-[A-Z]{2})?$/ =~ locale

    filter_file = args[3]
    raise UsageError, "File '#{filter_file}' does not exist." if filter_file && !File.exist?(filter_file)
    filter = DisplayNameCommon.load_filter(filter_file)

    return rdf_file, display_names_input_file, locale, filter, n3_output_file
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @rdf_file, @display_names_input_file, @locale, @filter, @n3_output_file = sanity_check_arguments(args)
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
        DISPLAY_NAME_URI => :display_name,
        }
      })

    solutions = DisplayNameCommon.new(@rdf_file).process(query, &@filter)

    display_names = IO.readlines(@display_names_input_file)
    
    raise "Number of display_names (#{display_names.length}) doesn't match number of URIs (#{solutions.length})" unless display_names.length == solutions.length

	graph = Graph.new
   	solutions.zip(display_names).each do |data|
   		s = data[0].prop
   		p = DISPLAY_NAME_URI
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
  inserter = DisplayNameInserter.new(ARGV)
  inserter.process() 
end
