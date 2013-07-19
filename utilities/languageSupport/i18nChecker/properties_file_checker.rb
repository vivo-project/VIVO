=begin
--------------------------------------------------------------------------------

Read and interpret a properties file. Accept a default version, if appropriate.

Warn if a specialized file has no default version.
Warn about duplicate keys, keys with empty values.
Warn about file reference values with no corresponding file
Warn about keys that do not appear in the default version.
If the "complete" flag is set, 
  Warn if the default version is not found.
  Warn about missing keys, compared to the default version.

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
  attr_reader :value

  def initialize(line, key, value)
    @line = line
    @key = key
    @value = value
  end
end

class PropertiesFileChecker
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # What path would represent the default version of this property file?
  #
  def figure_rootpath()
    name = File.basename(path)
    dirname = File.dirname(path)
    extname = File.extname(path)
    raise("Invalid property file name: '#{path}': too many underscores.") if name.count("_") > 2
    
    first_underscore = name.index('_')
    if first_underscore
      @rootpath = File.join(dirname, name[0, first_underscore] + extname)
    else
      @rootpath = path
    end
  end
  
  #
  # Is this a default version or a locale-specific version?
  #
  def is_root?()
    @rootpath == @path
  end

  def check_for_faux_continuations(lines)
    ln = 0
    lines.map do |line|
      ln += 1
      if /(\\) +$/.match(line)
        @warnings << Warning.new(ln, "On a continuation line, the \\ must not be followed by spaces.")
        $` + $1
      else
        line
      end
    end
  end

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

  def check_for_root_file()
    @warnings << Warning.new(0, "Found no root file '#{File.basename(@rootpath)}'") if !is_root?() && !@root
  end
    
  def scan_for_empty_values()
    @properties.values.each do |p|
      @warnings << Warning.new(p.line, "Value for '#{p.key}' is empty.") if p.value.empty?
    end
  end

  def scan_for_invalid_file_references()
    @properties.values.each do |p|
      if /@@file\s+(.*)/.match(p.value)
        file_reference = $1.strip
        dir = File.dirname(@path)
        path = File.join(dir, file_reference)
        unless File.file?(path)
          @warnings << Warning.new(p.line, "Invalid file reference '#{p.value}': file not found") 
        end
      end
    end
  end
  
  def scan_for_properties_not_present_in_root()
    root_name = File.basename(@root.path)
    extra_keys = @properties.keys - @root.properties.keys
    extra_keys.each do |key|
      p = @properties[key]
      @warnings << Warning.new(p.line, "Property '#{key}' is not present in root file '#{root_name}'")
    end
  end
  
  def scan_for_properties_not_present_in_derived
    root_name = File.basename(@root.path)
    next_line = @properties.values.max {|a, b| a.line <=> b.line}.line + 1
    missing_keys = @root.properties.keys - @properties.keys
    missing_keys.sort.each do |key|
      @warnings << Warning.new(next_line, "No value to override '#{key}' in the root file '#{root_name}'")
    end
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(path)
    @path = path
    @root = nil
    @rootpath = nil
    @warnings = []
    @properties = {}

    figure_rootpath()
    lines = IO.readlines(@path)
    lines = check_for_faux_continuations(lines)
    lines = join_continuation_lines(lines)
    read_properties(lines)
  end

  def try_to_set_root(root)
    if !is_root?
      if root.path == @rootpath
        @root = root
      end
    end
  end

  def path()
    @path
  end
  
  def properties()
    @properties
  end
  
  def warnings()
    @warnings
  end

  #
  # Analyze the properties, and say what we found.
  #
  def report(complete, summary)
    check_for_root_file() if complete
    scan_for_empty_values()
    scan_for_invalid_file_references()
    scan_for_properties_not_present_in_root() if @root
    scan_for_properties_not_present_in_derived() if complete && @root

    puts "   Properties file '#{@path}', #{@properties.size()} properties"

    if !@warnings.empty?
      if summary
        puts "      #{@warnings.size} warnings."
      else
        @warnings.sort! {|a, b| a.line <=> b.line}
        @warnings.each do |w|
          puts "      line #{w.line}: #{w.message}"
        end
      end
    end
  end
end
