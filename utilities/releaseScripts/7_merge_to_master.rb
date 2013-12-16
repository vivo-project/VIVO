=begin
--------------------------------------------------------------------------------

Merge the maintenance branches into the master branchs, and create the final 
release tags.

This will only work if the release candidate is "final", and if the maintenance
branches already exist.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Merge the maintenance branch to the master branch and create the tag.
#
def merge_branch_to_master(branch, tag, message, repo_path)
	Dir.chdir(repo_path) do |path|
		cmds = [
				"git checkout master",
				"git merge --no-ff -Xtheirs #{branch}",
				"git tag -a -f #{tag} -m '#{message}'"
				]
		cmds.insert(0, "git branch master origin/master") unless branch_exists?(path, "master")
		approve_and_execute(cmds, "in #{path}")
	end
end

#
# ------------------------------------------------------------------------------------
# Main method
# ------------------------------------------------------------------------------------
#

begin
	branch = Settings.branch_name
	candidate_label = Settings.confirm_candidate_label(Settings.candidate_label)
	tag = Settings.tag_name
	message = Settings.tag_message
	vivo_path = Settings.vivo_path
	vitro_path = Settings.vitro_path

	raise BadState.new("Only the final release gets merged to the master branch.") unless candidate_label == "final"
	raise BadState.new("Branch #{branch} doesn't exist in VIVO.") unless branch_exists?(vivo_path, branch) 
	raise BadState.new("Branch #{branch} doesn't exist in Vitro.") unless branch_exists?(vitro_path, branch) 
	
	get_permission_and_go("OK to merge the #{tag} tags to the master branches?") do
		puts "Merging tags"
		merge_branch_to_master(branch, tag, message, vivo_path)
		merge_branch_to_master(branch, tag, message, vitro_path)
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
