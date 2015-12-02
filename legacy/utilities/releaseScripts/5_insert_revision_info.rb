=begin
--------------------------------------------------------------------------------

Figure the revision info and store it in the export directory, for both VIVO
and Vitro.

If the tags don't exist in either repository, or if the export directory is not
populated, complain.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Get the revision information from Git and store it in the export directory.
#
def create_revision_info(git_path, info_file_path, tag)
	Dir.chdir(git_path) do |path|
		commit = `git show-ref --tags --hash=7 #{tag}`.strip
		puts "Writing '#{tag} ~ #{commit}' to #{info_file_path}" 
		File.open(info_file_path, "w") do |f|
			f.puts tag
			f.puts commit
		end
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
	
	raise BadState.new("Tag #{tag} doesn't exist in VIVO.") unless tag_exists?(vivo_path, tag) 
	raise BadState.new("Tag #{tag} doesn't exist in Vitro.") unless tag_exists?(vitro_path, tag) 
	raise BadState.new("Files have not been exported to #{export_dir}") unless File.directory?(export_dir)
	
	if File.exist?(vivo_revision_info_path) || File.exist?(vitro_revision_info_path)
		p = "OK to overwrite revision_info at these paths? \n    #{vivo_revision_info_path} \n    #{vitro_revision_info_path}  ?"
	else
		p = "OK to write revision_info at these paths? \n    #{vivo_revision_info_path} \n    #{vitro_revision_info_path}  ?"
	end

	get_permission_and_go(p) do
		puts "Building revision info"
		create_revision_info(vivo_path, vivo_revision_info_path, tag)
		create_revision_info(vitro_path, vitro_revision_info_path, tag)
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
