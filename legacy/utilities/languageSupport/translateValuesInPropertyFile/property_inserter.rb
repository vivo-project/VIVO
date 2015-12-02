#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------

Builds a property file, using an existing property file as a template, but 
getting the property values from a text file of translated text 
(see property_stripper.rb) and optionally a partial file of translated properties.

So, if you have a template file of English-language properties (e.g. all.properties), 
this will read the file into a properties structure. The text file of translated 
values is presumed to have one value per line, associated with the alphabetized 
list of keys from the template file. The translated values will replace the orignal 
values, with the exception that any value that starts with @@file will not be 
replaced.

If a partially translated file is provided, it will be read and used to replace 
any translated values from the text file, which are assumed to be weaker. Note
that this is true of @@file values as well, which are presumed to be corrected
for the language.

Any @@file values that are not overridden by the partial translation will result in 
a warning to stderr.

Finally, the template file is processed again, replacing the existing values with
the translated values, but keeping the same comment and spacing structure. 

On the command line provide the path to the tempate file, the text file, and 
optionally the partial translation. E.g.:

	property_inserter.rb '../../all.properties' translated.txt '../../all_es.properties' 'all_es.properties'

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require "property_common"

class PropertyInserter
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def sanity_check_arguments(args)
    raise "usage is: property_inserter.rb <template_file> <translated_values_file> [partial_translation] <output_file> [ok]" unless (3..5).include?(args.length)

	if args[-1].downcase == 'ok'
	  ok = true
	  args.pop
	else
	  ok = false
    end
        
    output_file = args.pop
    raise "File '#{output_file}' already exists. specify 'ok' to overwrite it." if File.exist?(output_file) && !ok

    template_file = args[0]
    raise "File '#{template_file}' does not exist." unless File.exist?(template_file)

    translated_values_file = args[1]
    raise "File '#{translated_values_file}' does not exist." unless File.exist?(translated_values_file)

    partial_translation = args[2]
    raise "File '#{partial_translation}' does not exist." if partial_translation && !File.exist?(partial_translation)

    return template_file, translated_values_file, partial_translation, output_file
  end
  
  def read_template_file()
    PropertiesFile.new(@template_file).properties
  end
  
  def read_and_merge_translated_values()
    lines = IO.readlines(@translated_values_file)
    raise "Number of lines in the translated values file (#{lines.size}) does not match the number of properties in the template file (#{@properties_map.size})." unless lines.size == @properties_map.size
    count = 0
    @properties_map.keys.sort.zip(lines) do |a|
      key, value = a
      unless @properties_map[key].value.start_with?("@@file")
        @properties_map[key].value = value
        count += 1
      end
    end
    puts "Merged #{count} translated values."
  end
  
  def read_and_merge_partial_translation()
    count = 0
    if @partial_translation
      @partial_map = PropertiesFile.new(@partial_translation).properties
      @partial_map.keys.each do |key|
        @properties_map[key].value = @partial_map[key].value
        count += 1
      end
    end
    puts "Overrode #{count} from partial translation."
  end
  
  def write_result()
    template_lines = merge_continuation_lines(IO.readlines(@template_file))
    File.open(@output_file, 'w') do |f|
      template_lines.each do |line|
        if line.length == 0 || line[0] == ?# || line[0] == ?! 
          # copy blank lines, and lines starting with '#' or '!'.
          f.puts line
        elsif line =~ /(.*?)(\s*[=:]\s*)(.*)/
          # key and value are separated by '=' or ':' and optional whitespace.
          key = $1.strip
          f.puts "#{$1}#{$2}#{@properties_map[key].value}"
        else
          # No '=' or ':' means that the value was empty.
          key = line.strip;
          if @properties_map[key]
            f.puts "#{key} = #{@properties_map[key].value}"
          else
            f.puts line
          end
        end
      end
    end
  end
  
  def merge_continuation_lines(lines)
    (lines.size()-1).downto(0) do |i|
      if /(.*)\\$/.match(lines[i])
        lines[i] = $1 + lines[i+1].lstrip()
        lines.delete_at(i+1)
      end
    end
    return lines
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @template_file, @translated_values_file, @partial_translation, @output_file = sanity_check_arguments(args)
  end
  
  def process()
    @properties_map = read_template_file()
    read_and_merge_translated_values()
    read_and_merge_partial_translation()
    write_result()
    puts "Wrote #{@properties_map.length} values to '#{@output_file}'"
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
  inserter = PropertyInserter.new(ARGV)
  inserter.process() 
end
