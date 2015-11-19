=begin
--------------------------------------------------------------------------------

A utility that scans the code base for the presence of URIs that were made 
obsolete by the ISF transition.

Accept a file of obsolete URIs.
	Blank lines or lines beginning with '#' are comments. 
	Each non-comment line contains an obsolete URI.

Accept a file of known exceptions.
	Blank lines or lines beginning with '#' are comments.
	Each non-comment line is in one of these forms:
		.xxx - denotes an extension that is exempt from scanning
		[filepath] - denotes a path, relative to the codebase root, of a 
				file that should not be scanned.
		[filepath] [line number] - denotes a particular line in a file for
				which no error should be reported. 
		[filepath] [line number] [uri] - denotes a particular line in a file
				on which the given uri will not be reported.
	
The command line will look like this:
	ruby obsoleteUriChecker.rb <directory_root> <obsolete_uri_file> <known_exceptions_file> [complete]
Where:
	directory_root - the path to the top if the directory tree we are scanning
	obsolete_uri_file - the path to the file that lists the obsolete URIs
	known_exceptions_file - the path to the file that lists the events that we should ignore
	complete - optional parameter; if present, check for :localname as well as for the full URL.
	 
E.g.:
	ruby obsoleteUriChecker.rb ../../.. ../../../productMods/WEB-INF/ontologies/update/diff.tab.txt known_exceptions.txt complete 

--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require 'known_exceptions'
require 'obsolete_uris'
require 'report'
require 'event'
require 'directory_walker'

class UsageError < StandardError; end

class ObsoleteUriChecker
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------

  #
  # Parse the arguments and complain if they don't make sense.
  #
  def parse_arguments(args)
    raise UsageError, "usage is: obsoleteUriChecker.rb <directory_root> <obsolete_uri_file> <known_exceptions_file> [complete]" unless (3..4).include?(args.length)

	if args[3]
		raise UsageError, "If provided, the 4th argument must be 'complete'" unless args[3].downcase == 'complete'
		complete = true
	else
		complete = false
	end
	
    directory_root = args[0]
    raise UsageError, "Directory '#{directory_root}' does not exist." unless File.exist?(directory_root)
    raise UsageError, "Directory '#{directory_root}' is not a directory." unless File.directory?(directory_root)

    obsolete_uri_file = args[1]
    raise UsageError, "File '#{obsolete_uri_file}' does not exist." unless File.exist?(obsolete_uri_file)
    obsolete_uris = ObsoleteUris.new(obsolete_uri_file)

    known_exceptions_file = args[2]
    raise UsageError, "File '#{known_exceptions_file}' does not exist." unless File.exist?(known_exceptions_file)
    known_exceptions = KnownExceptions.new(directory_root, known_exceptions_file)

    return directory_root, obsolete_uris, known_exceptions, complete
  end
  
  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  def initialize(args)
    @directory_root, @obsolete_uris, @known_exceptions, @complete = parse_arguments(args)
    @report = Report.new(args, @directory_root) 
  rescue UsageError => e
    puts "\n----------------\nUsage error\n----------------\n\n#{e}\n\n----------------\n\n"
    exit
  rescue ObsoleteUrisError => e
    puts "\n----------------\nObsolete Uris file is invalid\n----------------\n\n#{e}\n\n----------------\n\n"
    exit
  rescue KnownExceptionsError => e
    puts "\n----------------\Known Exceptions file is invalid\n----------------\n\n#{e}\n\n----------------\n\n"
    exit
  end

  def scan()
    walker = DirectoryWalker.new(@directory_root, @obsolete_uris, @known_exceptions, @report, @complete)
    walker.walk
  end

  def report()
    @report.report()
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
  checker = ObsoleteUriChecker.new(ARGV)
  checker.scan()
  checker.report()
end
