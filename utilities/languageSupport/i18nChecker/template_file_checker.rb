=begin
--------------------------------------------------------------------------------

Read a freemarker template and looks for likely i18n problems.

Warn about visible text that contains other than blank space or Freemarker expressions.
Visible text is:
  Anything that is not inside a tag and not between <script> or <style> tags
  title="" attributes on any tags
  alert="" attributes on <img> tags
  alt=""   attributes on <img> tags
  value="" attributes on <input> tags with submit attributes
  
a Freemarker express is enclosed in ${}

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

class TemplateFileChecker
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  
  def blanks(length)
    ' '.ljust(length, ' ')
  end
  
  def replace_comments(raw)
    raw.gsub(/<!--.*?-->/m) {|s| blanks(s.size)}
  end
  
  def replace_script_tags(raw)
    raw.gsub(/<script.*?>.*?<\/script>/m) {|s| blanks(s.size)}
  end
  
  def replace_style_tags(raw)
    raw.gsub(/<style.*?>.*?<\/style>/m) {|s| blanks(s.size)}
  end
  
  def replace_freemarker_comments(raw)
    raw.gsub(/<#--.*?-->/m) {|s| blanks(s.size)}
  end
  
  def replace_freemarker_tags(raw)
    raw.gsub(/<#.*?>/m) {|s| blanks(s.size)}
  end
  
  def replace_freemarker_expressions(raw)
    dirty = raw
    while /\$\{[^\{\}]*\}/m.match(dirty)
      dirty = $` + blanks($&.size) + $'
    end
    dirty
  end
  
  def remove_entities(raw)
    raw.gsub(/&[a-zA-Z]+;/, '')
  end
  
  def launder(raw)
    replace_script_tags(
      replace_style_tags(
        replace_freemarker_expressions(
          replace_freemarker_tags(
            replace_comments(
              replace_freemarker_comments(raw)))))) 
  end
  
  def load(path)
    IO.open(File.new(path).fileno) do |f|
      @contents = f.read()
      @clean_contents = launder(@contents)
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
  
  def text_for_display(raw)
    clean = raw.gsub(/[\n\r]/m, ' ').strip
    if clean.size < 50
      clean
    else
      clean[0..50] + "..."
    end
  end
  
  def scan(regexp, group_index, message)
    @clean_contents.gsub(regexp) do |s|
      offset = $~.begin(group_index)
      value = $~[group_index]
      if contains_words?(value)
        @warnings << Warning.new(line_number(offset), "#{message}: '#{text_for_display(value)}'") 
      end
      s 
    end
  end

  def contains_words?(raw)
    remove_entities(raw).count('a-zA-Z') > 0
  end
  
  def scan_for_words_outside_of_tags()
    scan(/>\s*([^><]+)</m, 1, "Words found outside of tags")
  end
  
  def scan_for_title_attributes()
    scan(/<[^>]*title=(["'])\s*([^>]*?)\s*\1.*?>/mi, 2, "Words found in title attribute of an HTML tag")
  end
  
  def scan_for_alert_attributes()
    scan(/<img\b[^>]*alert=(["'])\s*([^>]*?)\s*\1.*?>/mi, 2, "Words found in alert attribute of <img> tag")
  end
  
  def scan_for_alt_attributes()
    scan(/<img\b[^>]*alt=(["'])\s*([^>]*?)\s*\1.*?>/mi, 2, "Words found in alt attribute of <img> tag")
  end
  
  def scan_for_value_attributes_on_submit_tags()
    scan(/<input\b[^>]*type=["']submit["'][^>]*value=(["'])\s*([^'">]*?)\s*\1.*?>/mi, 2, "Words found in value attribute of <input type='submit'> tag")
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(path)
    @path = path
    @contents = ''
    @clean_contents = ''
    @line_offsets = [0]
    @warnings = []
    
    load(path)
    scan_for_words_outside_of_tags()
    scan_for_title_attributes()
    scan_for_alert_attributes()
    scan_for_alt_attributes()
    scan_for_value_attributes_on_submit_tags()
  end
  
  def report(summary)
    puts "   Template file '#{@path}'"
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
    $stdout.flush
  end
  
  def warnings()
    @warnings
  end
end
