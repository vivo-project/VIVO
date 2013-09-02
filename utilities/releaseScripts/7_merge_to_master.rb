=begin
--------------------------------------------------------------------------------

Merge the new tags into the master branches.

This will only work if the tag is present, and if the release candidate is "final".

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# Merge the tag to the master branch.
#
def merge_tag_to_master(tag, repo_path)
	Dir.chdir(repo_path) do |path|
		approve_and_execute([
				"git checkout master", 
				"git merge #{tag}"
				])
	end
end

#
# ------------------------------------------------------------------------------------
# Main method
# ------------------------------------------------------------------------------------
#

begin
	candidate_label = Settings.confirm_candidate_label(Settings.candidate_label)
	tag = Settings.tag_name
	vivo_path = Settings.vivo_path
	vitro_path = Settings.vitro_path

	raise BadState.new("Only the final release gets merged to the master branch.") unless candidate_label == "final"	
	raise BadState.new("Tag #{tag} doesn't exist in VIVO.") unless tag_exists?(vivo_path, tag) 
	raise BadState.new("Tag #{tag} doesn't exist in Vitro.") unless tag_exists?(vitro_path, tag) 
	raise BadState.new("Tag has already been merged to master branch in VIVO." if tag_commit(tag, vivo_path) == master_commit(vivo_path)
	raise BadState.new("Tag has already been merged to master branch in Vitro." if tag_commit(tag, vitro_path) == master_commit(vitro_path)
	
	puts
	yn = prompt("OK to merge the #{tag} tags to the master branches? (y/n)")
	if yn.downcase == 'y'
		puts
		puts "Merging tags"
		merge_tag_to_master(tag, vivo_path)
		merge_tag_to_master(tag, vitro_path)
		puts
	else
		puts
		puts "OK - forget it."
		puts
	end
rescue BadState
	puts "#{$!.message} - Aborting."
end
