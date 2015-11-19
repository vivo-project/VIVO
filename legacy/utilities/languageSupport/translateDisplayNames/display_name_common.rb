#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end 

require 'rubygems'
require 'rdf'
require 'rdf/rdfxml'
require 'rdf/ntriples'
require 'rdf/n3'

include RDF

DISPLAY_NAME_URI = RDF::URI.new("http://vitro.mannlib.cornell.edu/ns/vitro/ApplicationConfiguration#displayName")

class UsageError < StandardError; end
class FilterError < StandardError; end

    
class DisplayNameCommon
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def self.load_filter(filter_file)
    return lambda{|s| true} unless filter_file
    return eval(IO.read(filter_file))
  rescue
    raise FilterError.new($!.message)
  end
  
  def initialize(rdf_file, &filter)
    @filter = filter.nil? ? lambda{true} : filter
    @graph = Graph.load(rdf_file)
  end
  
  def process(query, &filter)
    solutions = query.execute(@graph)
    solutions.filter!(&filter)
    solutions.order(:prop)
  end
end