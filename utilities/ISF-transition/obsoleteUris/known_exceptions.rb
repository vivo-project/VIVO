require 'pathname'

class KnownExceptionsError < StandardError; end

class GlobSkipper
  def initialize(root_path, glob)
    @root_path = root_path
    @glob = glob
  end
  
  def relativize(path)
    Pathname.new(path).relative_path_from(Pathname.new(@root_path)).to_s
  end

  def skip?(path, line, uri)
    return File.fnmatch(@glob, relativize(path))
  end
end

class ExtensionSkipper
  def initialize(extension)
    @extension = extension
  end

  def skip?(path, line, uri)
    return File.extname(path) == @extension
  end
end 

class PathSkipper
  def initialize(root_path, relative_path)
    @root_path = root_path
    @absolute_path = File.expand_path(relative_path, @root_path)
  end
  
  def skip?(path, line, uri)
    return @absolute_path == File.expand_path(path, @root_path)
  end
end

class LineSkipper
  def initialize(root_path, relative_path, line_number)
    @inner = PathSkipper.new(root_path, relative_path)
    @line_number = line_number
  end
  
  def skip?(path, line, uri)
    return @inner.skip?(path, line, uri) && line == @line_number
  end
end

class KnownExceptions
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  
  def parse_file(file)
    skippers = []
    File.readlines(file).each do |line|
      # ignore blank lines, and lines starting with '#' or '!'.
      line.strip!
      next if line.length == 0 || line[0..0] == '#' || line[0] == ?!

#      if line =~ /^\.[^\/]*$/
#        skippers << ExtensionSkipper.new(line)
      if line =~ /^(\S+)\s*$/
#        skippers << PathSkipper.new(@root_path, $1)
        skippers << GlobSkipper.new(@root_path, $1)
      elsif line =~ /^(\S+)\s*(\d+)\s*$/
        skippers << LineSkipper.new(@root_path, $1, $2.to_i)
      else
        raise "BOGUS line in known_exceptions file: '#{line}'"
      end
    end
    skippers
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(root_path, file)
    @root_path = File.expand_path(root_path)
    @skippers = parse_file(file)
  end
  
  def skip?(file, line_number = -1, string = "@!#IMPOSSIBLE#!@")
    @skippers.each() do |skipper|
      if line_number == -1
        next if skipper.is_a?(LineSkipper)
      else
        next if skipper.is_a?(ExtensionSkipper) || skipper.is_a?(PathSkipper) || skipper.is_a?(GlobSkipper)
      end

      if skipper.skip?(file, line_number, string)
        return true
      end
    end
    false
  end
end
