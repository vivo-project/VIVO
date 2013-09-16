=begin
--------------------------------------------------------------------------------

Push any branches, tags, or merges back to GitHub.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Merge the maintenance branch to the master branch and create the tag.
#
def push_to_origin(repo_path)
	Dir.chdir(repo_path) do |path|
		approve_and_execute(["git push --all", "git push --tags"], "in #{path}")
	end
end

#
# ------------------------------------------------------------------------------------
# Main method
# ------------------------------------------------------------------------------------
#

begin
	vivo_path = Settings.vivo_path
	vitro_path = Settings.vitro_path
	
	get_permission_and_go("OK to push changes to the origin?") do
		puts "Merging tags"
		push_to_origin(vivo_path)
		push_to_origin(vitro_path)
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
