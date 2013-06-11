#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end 

require 'rubygems'
require 'rdf'
require 'rdf/rdfxml'
require 'rdf/ntriples'

include RDF
    
class LabelCommon
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

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