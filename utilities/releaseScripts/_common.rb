=begin
--------------------------------------------------------------------------------

Methods and classes used by the scripts.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

#
# ------------------------------------------------------------------------------------
# This exception class says that we aren't happy with the state of things.
# ------------------------------------------------------------------------------------
#

class BadState < Exception
end

#
# ------------------------------------------------------------------------------------
# A class with methods for handling the settings.
# ------------------------------------------------------------------------------------
#

class Settings
	#
	# The settings themselves: getters, setters, and validators.
	#
	
	def self.base_directory
		`git config --get --global vivo.release.basedirectory`.strip
	end
	
	def self.base_directory=(dir)
		`git config --global vivo.release.basedirectory #{dir}`
	end
	
	def self.confirm_base_directory(path)
		expanded = File.expand_path(path)
		vivo_git = File.expand_path("VIVO/.git", expanded)
		vitro_git = File.expand_path("Vitro/.git", expanded)
		raise BadState.new("#{expanded} is not a directory") unless File.directory?(expanded)
		raise BadState.new("#{expanded} doesn't contain a VIVO repository") unless File.directory?(vivo_git)
		raise BadState.new("#{expanded} doesn't contain a Vitro repository") unless File.directory?(vitro_git)
		expanded
	end


	def self.username
		`git config --get --global user.name`.strip
	end
	
	def self.username=(name)
		`git config --global user.name #{name}`
	end

	def self.confirm_username(name)
		# any string will do.
		name
	end

	
	def self.email
		`git config --get --global user.email`.strip
	end
	
	def self.email=(address)
		`git config --global user.email #{address}`
	end

	def self.confirm_email(address)
		# any string will do.
		address
	end

	
	def self.release_label
		`git config --get --global vivo.releaselabel`.strip
	end
	
	def self.release_label=(label)
		`git config --global vivo.releaselabel #{label}`
	end
	
	def self.confirm_release_label(label)
		raise BadState.new("Incorrect format for release label - '#{label}'") unless label =~ /^\d\.\d(\.\d)?$/
		label
	end


	def self.candidate_label
		`git config --get --global vivo.candidatelabel`.strip
	end
	
	def self.candidate_label=(label)
		`git config --global vivo.candidatelabel #{label}`
	end
	
	def self.confirm_candidate_label(label)
		raise BadState.new("Incorrect format for candidate label - '#{label}'") unless label =~ /^(rc\d+|tc\d+|final)$/
		label
	end

	#
	# Values derived from the settings.
	#

	# The name of the maintenance branch. Looks like "maint-rel-4.2" even for releases like "4.2.1"
	# Test candidates for major releases are built on the develop branch.
	def self.branch_name
		if is_test_candidate?()
			"develop"
		else
			release_label = Settings.confirm_release_label(Settings.release_label)
			major_release = release_label[0..2]
			"maint-rel-#{major_release}"
		end
	end
	
	# The name of the Git tag. Looks like "rel-1.9-tc2" or "rel-1.9" (for final)
	def self.tag_name
		release_label = Settings.confirm_release_label(Settings.release_label)
		label = Settings.confirm_candidate_label(Settings.candidate_label)
		suffix = label == "final" ? "" : "-#{label}"
		"rel-#{release_label}#{suffix}"
	end
	
	# The message for the Git tag. Looks like "Release 1.9 rc5 tag"
	def self.tag_message
		release_label = Settings.confirm_release_label(Settings.release_label)
		candidate_label = Settings.confirm_candidate_label(Settings.candidate_label)
		"Release #{release_label} #{candidate_label} tag"
	end
	
	# Where is the local VIVO repository? Looks like "/Users/jeb228/git/VIVO"
	def self.vivo_path
		base_directory = Settings.confirm_base_directory(Settings.base_directory)
		File.expand_path("VIVO", base_directory)
	end
	
	# Where is the local Vitro repository? Looks like "/Users/jeb228/git/Vitro"
	def self.vitro_path
		base_directory = Settings.confirm_base_directory(Settings.base_directory)
		File.expand_path("Vitro", base_directory)
	end
	
	# Where will the distribution files be created? Looks like "/Users/jeb228/git/release_4.9/tc5/vivo-rel-4.9-tc5"
	def self.export_dir
		base_directory = Settings.confirm_base_directory(Settings.base_directory)
		release_label = Settings.confirm_release_label(Settings.release_label)
		candidate_label = Settings.confirm_candidate_label(Settings.candidate_label)
		tag_name = Settings.tag_name
		File.expand_path("release_#{release_label}/#{candidate_label}/vivo-#{tag_name}", base_directory)
	end
	
	# Where to store the file for revision info in VIVO
	def self.vivo_revision_info_path
		File.expand_path("revisionInfo", Settings.export_dir)
	end
	
	# Where to store the file for revision info in VIVO
	def self.vitro_revision_info_path
		File.expand_path("vitro-core/revisionInfo", Settings.export_dir)
	end
	
	# Looks like "vivo-rel-4.9-tc3" or "vivo-rel-4.9" for final
	def self.vivo_distribution_filename
		"vivo-#{Settings.tag_name}"
	end
	
	# Looks like "vitro-rel-4.9-tc3" or "vitro-rel-4.9" for final
	def self.vitro_distribution_filename
		"vitro-#{Settings.tag_name}"
	end
end

#
# ------------------------------------------------------------------------------------
# Confirmation methods.
# ------------------------------------------------------------------------------------
#

def is_test_candidate?()
	Settings.confirm_candidate_label(Settings.candidate_label)[0..0] == 't'
end

def is_final_release?()
	Settings.confirm_candidate_label(Settings.candidate_label) == 'final'
end

#
# ------------------------------------------------------------------------------------
# General-purpose methods.
# ------------------------------------------------------------------------------------
#

def prompt(p)
    print("#{p} ")
    gets.strip
end

def echo_command(c)
	puts ">>>>>> #{c}"
	`#{c}`
end

def get_permission_and_go(p)
	puts
	yn = prompt("#{p} (y/n)")
	if yn.downcase == 'y'
		puts
		yield
		puts
	else
		puts
		puts "OK - forget it."
		puts
	end	
end

def approve_and_execute(cmds, prompt="")
	if prompt.empty?
		puts "Execute these commands?"
	else
		puts "Execute these commands? (#{prompt})"
	end
		
	puts ">>>>> #{cmds.join("\n>>>>> ")}"
	yn = prompt "(y/n)"
	raise BadState.new("OK") if (yn.downcase != 'y')
	
	cmds.each do |cmd|
		puts ">>>>> #{cmd}"
		puts `#{cmd}`
		raise BadState.new("Command failed: code #{$?.exitstatus}") unless $?.success?
	end
	puts
end

def branch_exists?(dir, branch)
	Dir.chdir(dir) do |path|
		re = Regexp.new("\\b#{branch}\\b")
		`git branch`.index(re)
	end
end

def remote_branch_exists?(dir, branch)
	Dir.chdir(dir) do |path|
		re = Regexp.new("remotes/origin/#{branch}\\b")
		`git branch -a`.index(re)
	end
end

def tag_exists?(dir, tag)
	Dir.chdir(dir) do |path|
		re = Regexp.new("^#{tag}$")
		`git tag`.index(re)
	end
end

