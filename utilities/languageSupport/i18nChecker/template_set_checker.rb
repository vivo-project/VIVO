=begin
--------------------------------------------------------------------------------

Look at a set of template paths, and remove any paths that represent 
language-specific templates. Warn if any of those templates are using the i18n() 
Freemarker method.

Note: any template path that ends in _xx.ftl or _xx_YY.ftl is assumed to be 
language-specific. As a heuristic, we will also assume that any template with 
the same path, but without the language-specifier, is also language-specific,
representing the default version of the template.

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

class TemplateSet
  attr_reader :root
  attr_reader :paths
  attr_reader :warnings
  
  def initialize(root)
    @root = root
    @paths = []
    @warnings = []
  end
  
  def add_path(path)
    @paths << path
  end
  
  def add_warning(warning)
    @warnings << warning
  end
end

class TemplateSetChecker
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  def find_template_sets()
    find_language_specific_templates()
    find_default_language_templates()
    remove_from_remaining_paths()
  end
  
  def find_language_specific_templates()
    @paths.each() do |path|
      if is_language_specific(path)
        add_to_template_sets(path)
      end
    end
  end
  
  def is_language_specific(path)
    /_[a-z]{2}(_[A-Z]{2})?\.ftl$/ =~ path
  end
  
  def add_to_template_sets(path)
    root = root_path(path)
    set = @template_sets[root] || TemplateSet.new(root)
    set.add_path(path)
    @template_sets[root] = set
  end
  
  def root_path(path)
    if /(.*?)(_[a-z]{2}(_[A-Z]{2})?)?\.ftl$/ =~ path
      $1
    else
      path
    end
  end
  
  def find_default_language_templates()
    @paths.each() do |path|
      root = root_path(path)
      set = @template_sets[root]
      if set
        set.add_path(path) unless set.paths.include?(path)
      end
    end
  end
  
  def remove_from_remaining_paths()
    @remaining_paths = Array.new(@paths)
    @template_sets.each_value do |set|
      set.paths.each do |path|
        @remaining_paths.delete(path)
      end
    end
  end
  
  def scan_set(set)
    set.paths.each do |path|
      load(path)
      @contents.gsub(/i18n/) do |s|
        offset = $~.begin(0)
        set.add_warning(Warning.new(line_number(offset), "in #{path}: Call to i18n() in a language-specific template."))
	    s
      end
    end
  end
  
  def load(path)
    @line_offsets = []
    IO.open(File.new(path).fileno) do |f|
      @contents = f.read()
      pos = 0
      while found = @contents.index(/\n/, pos)
        pos = found + 1
        @line_offsets << pos
      end
    end
  end
  
  def line_number(offset)
    return @line_offsets.find_index() {|o| o > offset} || @line_offsets.size
  end
  
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(paths)
    @paths = paths
    @template_sets = {}
    @remaining_paths = []
    
    find_template_sets()
    @template_sets.each_value do |set|
      scan_set(set)
    end
  end
  
  def report(summary)
    @template_sets.each_value do |set|
      puts"   Template file set '#{set.paths.join("'\n	                  '")}'"
      if !set.warnings.empty?
        if summary
          puts "      #{set.warnings.size} warnings."
        else
          set.warnings.sort! {|a, b| a.line <=> b.line}
          set.warnings.each do |w|
            puts "      line #{w.line}: #{w.message}"
          end
        end
      end
    end
    $stdout.flush
  end
  
  def warnings_count()
    count = 0
    @template_sets.each_value do |set|
      count += set.warnings.size
    end
    count
  end
  
  def remaining_paths()
    @remaining_paths
  end
end
