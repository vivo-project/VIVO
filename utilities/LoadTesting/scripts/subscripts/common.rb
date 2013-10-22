#
# The path to the LoadTesting directory. Everything is based on this
# 
@home = File.expand_path("../..", File.dirname(__FILE__))

#
# 'require' should look in the scripts directory
#
$: << File.expand_path('scripts', @home)

# 
# convenience methods to access files
#
def version_file(path)
  "#{@home}/versions/#{@version_name}/#{path}"
end

def distro_file(path)
  "#{@home}/distros/#{@distro_name}/#{path}"
end

def site_file(path)
  "#{@home}/sites/#{@site_name}/#{path}"
end

def test_file(path)
  "#{@home}/testinfo/#{path}"
end

#
# All of the scripts need to load these parms. (Except _setVersion and _setTest)
#
require 'subscripts/loadParms'
