#!/usr/bin/ruby
=begin
--------------------------------------------------------------------------------

Some common routines used both by property_stripper and property_inserter

--------------------------------------------------------------------------------
=end 

class Warning
  attr_reader :line
  attr_reader :message
  
  def initialize(line, message)
    @line = line
    @message = message
  end
end

class Property
  attr_reader :line
  attr_reader :key
  attr_accessor :value

  def initialize(line, key, value)
    @line = line
    @key = key
    @value = value
  end
end

class PropertiesFile
  attr_reader :properties
  attr_reader :warnings
  
  def join_continuation_lines(lines)
    (lines.size()-1).downto(0) do |i|
      if /(.*)\\$/.match(lines[i])
        lines[i] = $1 + lines[i+1].lstrip()
        lines[i+1] = ''
      end
    end
    return lines
  end
  
  def read_properties(lines)
    ln = 0
    lines.each do |line|
      ln += 1
      line.strip!

      # ignore blank lines, and lines starting with '#' or '!'.
      next if line.length == 0 || line[0] == ?# || line[0] == ?!
      
      if line =~ /(.*?)\s*[=:]\s*(.*)/
        # key and value are separated by '=' or ':' and optional whitespace.
        key = $1.strip
        value = $2
      else
        # No '=' or ':' means that the value is empty.
        key = line;
        value = ''
      end
      
      if dupe = @properties[key]
        @warnings << Warning.new(ln, "Key '#{key}' is duplicated on line #{dupe.line}")
      else
        @properties[key] = Property.new(ln, key, value)
      end
    end
  end 
  
  def initialize(path)
    @properties = {}
    @warnings = []
    lines = IO.readlines(path)
    lines = join_continuation_lines(lines)
    read_properties(lines)
  end 
end
