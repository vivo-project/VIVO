#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------

Read a property file, sort the remainder alphabetically and write their values to 
a text file, one value per line.

The idea is that this file could be translated and the result could be used to 
create a new property file with property_inserter.rb

On the command line provide the path to the properties file. E.g.:

	property_stripper.rb '../../all.properties' output_file

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require "property_common"

class PropertyStripper
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def sanity_check_arguments(args)
    raise "usage is: property_stripper.rb <properties_file> <values_output_file> [ok]" unless (2..3).include?(args.length)

	if args[2].nil?
	  ok = false
	elsif args[2].downcase == 'ok'
	  ok = true
	else
	  raise "third argument, if present, must be 'ok'"
    end
        
    properties_file = args[0]
    raise "File '#{properties_file}' does not exist." unless File.exist?(properties_file)

    values_output_file = args[1]
    raise "File '#{values_output_file}' already exists. specify 'ok' to overwrite it." if File.exist?(values_output_file) && !ok

    return properties_file, values_output_file
  end
  
  def read_properties_file(properties_file)
    PropertiesFile.new(properties_file).properties
  end
  
  def write_values(values_output_file, properties)
    File.open(values_output_file, 'w') do |f|
      properties.keys.sort.each do |key|
        f.puts properties[key].value
      end
    end
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @properties_file, @values_output_file = sanity_check_arguments(args)
  end
  
  def process()
    @properties = read_properties_file(@properties_file)
    write_values(@values_output_file, @properties)
    puts "Wrote #{@properties.length} values to '#{@values_output_file}'"
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
  stripper = PropertyStripper.new(ARGV)
  stripper.process() 
end
