=begin
--------------------------------------------------------------------------------

A utility routine that scans I18n-related properties files and Freemarker 
templates for obvious issues.

Properties files:
	Warn if a specialized file has no default version.
	Warn about duplicate keys, keys with empty values.
	Warn about file reference values with no corresponding file
	Warn about keys that do not appear in the default version.
	If the "complete" flag is set, 
    Warn if the default version is not found.
		Warn about missing keys, compared to the default version.
		
Freemarker templates:
	Warn about visible text that contains other than blank space or Freemarker expressions.
	Visible text is:
		Anything that is not inside a tag and not between <script> tags
		title="" attributes on any tags
		alert="" attributes on <img> tags
		alt=""   attributes on <img> tags
		value="" attributes on <input> tags with submit attributes

On the command line, provide a file "glob" (don't allow the shell to interpret it)
and optional "complete" or "summary" flags. E.g.:

	i18nChecker.rb '../../themes/wilma/**/*' complete

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require 'properties_file_checker'
require 'template_file_checker'
require 'template_set_checker'

class I18nChecker
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def sanity_check_arguments(args)
    if ARGV.length == 0
      raise("No arguments - usage is: ruby i18nChecker.rb <file_spec_glob> [complete] [summary]")
    end
    
    file_spec = args[0]
    complete = false;
    summary = false;
    
    args[1..-1].each do |arg|
      if "complete" == arg.downcase()
        complete = true
      elsif "summary" == arg.downcase()
        summary = true
      else 
        raise("'#{arg}' is an invalid argument")
      end
    end

    puts "file_spec = '#{file_spec}', complete = #{complete}, summary = #{summary}"
    return file_spec, complete, summary
  end
  
  #
  # Go through the specified files and pick out the *.properties and *.ftl files.
  #
  def get_file_paths(file_spec)
    properties = []
    templates = []
    
    Dir.glob(file_spec) do |path|
      properties << path if File.extname(path) == '.properties'
      templates << path if File.extname(path) == '.ftl'
    end
    @total_files = properties.size + templates.size
    
    puts "Found #{properties.size} property files, #{templates.size} templates."
    return properties, templates
  end
  
  def process_properties_files(paths, complete, summary)
    checkers = []
    paths.each() do |path|
      checkers << PropertiesFileChecker.new(path)
    end
    
    checkers.each() do |child|
      checkers.each() do |root|
        child.try_to_set_root(root)
      end
    end
    
    checkers.each() do |checker|
      checker.report(complete, summary)
      @total_warnings += checker.warnings.size
    end
  end
  
  def process_template_files(paths, summary)
    ts = TemplateSetChecker.new(paths)
    ts.report(summary)
    paths = ts.remaining_paths
    @total_warnings += ts.warnings_count
    
    paths.each() do |path|
      tf = TemplateFileChecker.new(path)
      tf.report(summary)
      @total_warnings += tf.warnings.size
    end
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @total_files = 0
    @total_warnings = 0
    
    file_spec, complete, summary = sanity_check_arguments(args)
    properties, templates = get_file_paths(file_spec)
    process_properties_files(properties, complete, summary)
    process_template_files(templates, summary)
  end
  
  def summarize()
    puts "Found #{@total_warnings} warnings in #{@total_files} files."
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
  checker = I18nChecker.new(ARGV)
  checker.summarize()
end
