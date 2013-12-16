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

class UsageError < StandardError; end
class FilterError < StandardError; end
    
class LabelCommon
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