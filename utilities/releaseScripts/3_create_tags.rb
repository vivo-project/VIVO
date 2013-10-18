=begin
--------------------------------------------------------------------------------

Get the branch name and the tag name.

If either repository doesn't contain the branch, complain.
If either repository already contains the tag, complain.

Otherwise, Checkout the branch, pull the branch to the latest commit (if it is 
tracking a remote branch) and create the tag. Don't push.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Create a tag by this name in this repository.
#
def create_tag(dir, branch, tag, message)
	Dir.chdir(dir) do |path|
		cmds = ["git checkout #{branch}", 
				"git pull",
				"git tag -a #{tag} -m '#{message}'"
				]
		cmds.delete_at(1) unless remote_branch_exists?(path, branch)
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
	tag = Settings.tag_name
	message = Settings.tag_message
	vivo_path = Settings.vivo_path
	vitro_path = Settings.vitro_path
	
	raise BadState.new("Branch #{branch} doesn't exist in VIVO.") unless branch_exists?(vivo_path, branch) 
	raise BadState.new("Branch #{branch} doesn't exist in Vitro.") unless branch_exists?(vitro_path, branch) 
	raise BadState.new("Tag #{tag} already exists in VIVO.") if tag_exists?(vivo_path, tag) 
	raise BadState.new("Tag #{tag} already exists in Vitro.") if tag_exists?(vitro_path, tag) 

	get_permission_and_go("OK to create tags named '#{tag}' '#{message}'?") do
		puts "Creating tags"
		create_tag(vivo_path, branch, tag, message)
		create_tag(vitro_path, branch, tag, message)
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
