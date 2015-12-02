=begin
--------------------------------------------------------------------------------

Check for the required parameters of base directory, username, email address, 
revision label and candidate label.

Offer to run the licenser to be sure that we are ready to go.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
=end

$: << File.dirname(File.expand_path(__FILE__))
require '_common'

#
# ------------------------------------------------------------------------------------
# Runs the licenser against both VIVO and Vitro, and shows the results.
# ------------------------------------------------------------------------------------
#

class LicenserCaller
	def call_licenser(property_file)
		properties = PropertyFileReader.read(property_file)
		l = Licenser.new(properties)
		l.process
		l.report(properties)
		return l.success?
	end

	def initialize()
		require "#{Settings.vitro_path}/utilities/licenser/licenser"
		require "#{Settings.vitro_path}/utilities/licenser/property_file_reader"

		puts "Scanning VIVO..."
		vivo_success = call_licenser("#{Settings.vivo_path}/config/licenser/licenser.properties")
		puts "Scanning Vitro..."
		vitro_success = call_licenser("#{Settings.vitro_path}/webapp/config/licenser/licenser.properties")
		if vivo_success && vitro_success
		  puts "Licenser was successful"
		else
		  puts "Licenser found problems"
		end
	end
end

#
# Show the current value of the setting, ask for a replacement value, validate it, and set it.
#
def do_setting(getter, validator, setter, label, format="a string")
	v1 = getter.call
	currently = v1.empty? ? "Currently not set" : "Currently '#{v1}'"
	v2 = prompt "#{label}?\n(#{format})\n(#{currently})"
	
	v2 = v1 if v2.empty?
	v2 = validator.call(v2)

	if v2.empty?
		raise InputException.new("Can't run without #{label}")
	elsif v1 == v2
		puts "Keeping #{label} as '#{v2}'"
		puts
	else 
		puts "Setting #{label} to '#{v2}'"
		setter.call(v2)
		puts
	end
	v2
end

def get_base_directory()
	do_setting(
			Settings.method(:base_directory),
			Settings.method(:confirm_base_directory),
			Settings.method(:base_directory=), 
			"Git base directory", 
			"holds Vitro and VIVO repositories")
end

def get_user_name()
	do_setting(
			Settings.method(:username),
			Settings.method(:confirm_username),
			Settings.method(:username=), 
			"Git user.name")
end

def get_email()
	do_setting(
			Settings.method(:email),
			Settings.method(:confirm_email),
			Settings.method(:email=), 
			"Git user.email")
end

def get_release_label()
	do_setting(
			Settings.method(:release_label),
			Settings.method(:confirm_release_label),
			Settings.method(:release_label=), 
			"Release label", 
			"like '3.2' or '3.2.1'")
end

def get_candidate_label()
	do_setting(
			Settings.method(:candidate_label),
			Settings.method(:confirm_candidate_label),
			Settings.method(:candidate_label=), 
			"Release candidate label", 
			"like 'rc1' or 'tc3' or 'final'")
end

def run_licenser()
	puts "It's a good idea to check the licenses before proceeding."
	yn = prompt "Ready to run the licenser? (y/n)"
	if (yn.downcase == 'y')
		LicenserCaller.new()
	else
		puts "OK - forget it."
	end
end
	
#
# ------------------------------------------------------------------------------------
# Main method
# ------------------------------------------------------------------------------------
#

begin
	get_base_directory()
	get_user_name()
	get_email()
	get_release_label()
	get_candidate_label()
	run_licenser()
rescue BadState
	puts
	puts "#{$!.message} - Aborting."
	puts
end
