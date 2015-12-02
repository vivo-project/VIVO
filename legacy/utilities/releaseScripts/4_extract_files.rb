=begin
--------------------------------------------------------------------------------

Get the tag name.

If either repository doesn't contain the tag, complain.

Otherwise, checkout the tag, copy the files to an appropriate area. 

The files are specified so hidden files will not be copied, but this only works 
at the top levels. So the .git directories are omitted, as well as some Eclipse
artifacts and Mac OS artifacts. However, this only works at the top levels.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Get the VIVO files and the Vitro files, and remove the .git directories.
#
def export_files(vivo_path, vitro_path, tag, branch, export_dir)
	Dir.chdir(vivo_path) do |path|
		cmds = ["git checkout #{branch}", 
				"git pull",
				]
		cmds.delete_at(1) unless remote_branch_exists?(path, branch)
		approve_and_execute(cmds, "in #{path}")
	end
	
	Dir.chdir(vitro_path) do |path|
		cmds = ["git checkout #{branch}", 
				"git pull",
				]
		cmds.delete_at(1) unless remote_branch_exists?(path, branch)
		approve_and_execute(cmds, "in #{path}")
	end
	
	approve_and_execute([
			"rm -Rf #{File.expand_path("..", export_dir)}",
			"mkdir -pv #{export_dir}",
			"cp -R #{vivo_path}/* #{export_dir}",
			"mkdir -pv #{export_dir}/vitro-core",
			"cp -R #{vitro_path}/* #{export_dir}/vitro-core",
			])
end

#
# ------------------------------------------------------------------------------------
# Main method
# ------------------------------------------------------------------------------------
#

begin
	tag = Settings.tag_name
	branch = Settings.branch_name
	vivo_path = Settings.vivo_path
	vitro_path = Settings.vitro_path
	export_dir = Settings.export_dir
	
	raise BadState.new("Tag #{tag} doesn't exist in VIVO.") unless tag_exists?(vivo_path, tag) 
	raise BadState.new("Tag #{tag} doesn't exist in Vitro.") unless tag_exists?(vitro_path, tag) 

	if File.directory?(export_dir) 
		p = "OK to overwrite export area at #{export_dir} ?"
	else
		p = "OK to create export area at #{export_dir} ?"
	end
	
	get_permission_and_go(p) do
		puts "Building export area"
		export_files(vivo_path, vitro_path, tag, branch, export_dir)
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
