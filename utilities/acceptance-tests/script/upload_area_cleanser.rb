=begin
--------------------------------------------------------------------------------

Remove any files from the VIVO upload area, so we have a clean area for the next
suite of tests.

--------------------------------------------------------------------------------

Parameters:
  upload_directory
    The path to the upload area.

--------------------------------------------------------------------------------
=end
require 'fileutils'
require File.expand_path('property_file_reader', File.dirname(File.expand_path(__FILE__)))

class UploadAreaCleanser
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Confirm that the parameters are reasonable.
  #
  def sanity_checks_on_parameters()
    # Check that all necessary properties are here.
    raise("Properties file must contain a value for 'upload_directory'") if @upload_directory == nil

    if !File.exist?(@upload_directory)
      raise "Upload directory  '#{@upload_directory}' does not exist."
    end

    if !File.readable?(@upload_directory)
      raise "Upload directory  '#{@upload_directory}' is not readable."
    end

    if !File.directory?(@upload_directory)
      raise "Upload directory  '#{@upload_directory}' is not a directory."
    end

    if !File.writable?(@upload_directory)
      raise "Upload directory '#{@upload_directory}' is not writable."
    end
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  # Get the parameters and check them
  #
  def initialize(properties)
    @upload_directory = properties['upload_directory']
    sanity_checks_on_parameters()
  end

  # Cleanse the directory
  #
  def cleanse()
    FileUtils::rm_r(Dir.glob(File.expand_path('*', @upload_directory)), :verbose=>true)
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
  if ARGV.length == 0
    raise("No arguments - usage is: ruby upload_area_cleanser.rb <properties_file>")
  end
  if !File.file?(ARGV[0])
    raise "File does not exist: '#{ARGV[0]}'."
  end

  properties = PropertyFileReader.read(ARGV[0])

  uac = UploadAreaCleanser.new(properties)
  uac.cleanse()
end