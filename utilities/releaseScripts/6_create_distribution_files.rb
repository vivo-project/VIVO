=begin
--------------------------------------------------------------------------------

Create TAR and ZIP files for both VIVO and Vitro.

Complain if the files have not been exported, or the revision info doesn't exist.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Zip up the VIVO distribution. Extract the Vitro distribution and zip that.
#
def create_distribution_files(export_dir, vivo_filename, vitro_filename)
	export_parent_dir = File.dirname(export_dir)
	Dir.chdir(export_parent_dir) do |path|
		cmds = [
				"cp -r #{vivo_filename}/vitro-core #{vitro_filename}",
				"zip -rq #{vivo_filename}.zip #{vivo_filename}",
				"tar -czf #{vivo_filename}.tar.gz #{vivo_filename}",
				"zip -rq #{vitro_filename}.zip #{vitro_filename}", 
				"tar -czf #{vitro_filename}.tar.gz #{vitro_filename}"
				]
		cmds.insert(0, "rm -r #{vitro_filename}") if File.exist?(vitro_filename)
		approve_and_execute(cmds)
	end
end

#
# ------------------------------------------------------------------------------------
# Main method
# ------------------------------------------------------------------------------------
#

begin
	tag = Settings.tag_name
	vivo_path = Settings.vivo_path
	vitro_path = Settings.vitro_path
	export_dir = Settings.export_dir
	vivo_revision_info_path = Settings.vivo_revision_info_path
	vitro_revision_info_path = Settings.vitro_revision_info_path
	vivo_filename = Settings.vivo_distribution_filename
	vitro_filename = Settings.vitro_distribution_filename
	
	raise BadState.new("Files have not been exported to #{export_dir}") unless File.directory?(export_dir)
	raise BadState.new("Revision information file does not exist at #{vivo_revision_info_path}") unless File.exist?(vivo_revision_info_path)
	raise BadState.new("Revision information file does not exist at #{vitro_revision_info_path}") unless File.exist?(vitro_revision_info_path)
	

	get_permission_and_go("OK to create distribution files in #{export_dir} ?") do
		puts "Creating distribution files"
		create_distribution_files(export_dir, vivo_filename, vitro_filename)
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
