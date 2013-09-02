=begin
--------------------------------------------------------------------------------

Get the branch name.

If either repository already contains the branch, complain.

Otherwise, pull develop to the latest commit and create the branches. Don't push.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Create a branch by this name in this repository.
#
def create_branch(dir, branch)
	Dir.chdir(dir) do |path|
		approve_and_execute([
				"git checkout develop", 
				"git pull", 
				"git checkout -b #{branch}"
				], "in #{path}")
	end
end

#
# ------------------------------------------------------------------------------------
# Main method
# ------------------------------------------------------------------------------------
#

begin
	branch = Settings.branch_name
	vivo_path = Settings.vivo_path
	vitro_path = Settings.vitro_path
	
	raise BadState.new("Branch #{branch} already exists in VIVO.") if branch_exists?(vivo_path, branch) 
	raise BadState.new("Branch #{branch} already exists in Vitro.") if branch_exists?(vitro_path, branch) 

	puts
	yn = prompt("OK to create branches named '#{branch}' (y/n)")
	if yn.downcase == 'y'
		puts
		puts "Creating branches"
		create_branch(vivo_path, branch)
		create_branch(vitro_path, branch)
		puts
	else
		puts
		puts "OK - forget it."
		puts
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
