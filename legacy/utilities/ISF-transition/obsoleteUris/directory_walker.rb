require 'find'

class DirectoryWalker
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  def start_walking()
    Find.find(@directory_root) do |path|
      if FileTest.directory?(path)
        if File.basename(path).start_with?(".")
          Find.prune       # Don't look any further into this directory.
        elsif @known_exceptions.skip?(path)
          Find.prune
        else
          next
        end
      elsif @known_exceptions.skip?(path)
        Find.prune
      else
        scan_file(path)
      end
    end
  end
  
  def scan_file(path)
    @report.register_file(path)
    lines = File.readlines(path)
    lines.each_index do |index|
      line_number = index + 1
      line = lines[index].strip
      scan_line(path, line_number, line) unless @known_exceptions.skip?(path, line_number)
    end
  end
  
  def scan_line(path, line_number, line)
    @obsolete_uris.uris.each do |uri|
#     next if @known_exceptions.skip?(path, line_number, uri)
      @report.add_event(Event.new(path, line_number, line, uri)) if line =~ Regexp.new("\\b#{Regexp.quote(uri)}\\b") 
    end
    if @complete
      @obsolete_uris.localnames.each do |localname|
        term = ":#{localname}"
#       next if @known_exceptions.skip?(path, line_number, term)
        @report.add_event(Event.new(path, line_number, line, term)) if line =~ Regexp.new("#{Regexp.quote(term)}\\b") 
      end
    end
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(directory_root, obsolete_uris, known_exceptions, report, complete)
  	@directory_root = File.expand_path(directory_root)
  	@obsolete_uris = obsolete_uris
  	@known_exceptions = known_exceptions
  	@report = report
  	@complete = complete
  end
  
  def walk()
    start_walking()
  end
  
end
