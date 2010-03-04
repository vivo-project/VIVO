=begin
--------------------------------------------------------------------------------

Stop the vivo application, delete all mysql tables, and start the application
again.

--------------------------------------------------------------------------------

Parameters:
  -- the base URL of the vivo application: e.g. "http://localhost:8080/vivo"
  -- the username and password of a Tomcat account authorized as a "manager".
  -- the username and password of the MySQL account for the vivo application
  -- the name of the MySQL database for the vivo application

--------------------------------------------------------------------------------

What are we doing?
  -- Break the URL into parts, so we can get the base Tomcat URL and the name
     of the webapp.
  -- Tell Tomcat's manager to stop the app.
  -- Tell MySQL to drop the database and rebuild it.
  -- Tell Tomcat's manager to start the app.
--------------------------------------------------------------------------------
=end
require 'open-uri'
require 'property_file_reader'

class DatabaseCleanser
  # ------------------------------------------------------------------------------------
  private
  # ------------------------------------------------------------------------------------
  #
  # Confirm that the parameters are reasonable.
  #
  def sanity_checks_on_parameters()
    # Check that all necessary properties are here.
    raise("Properties file must contain a value for 'webapp_url'") if @webapp_url == nil
    raise("Properties file must contain a value for 'tomcat_username'") if @tomcat_username == nil
    raise("Properties file must contain a value for 'tomcat_password'") if @tomcat_password == nil
    raise("Properties file must contain a value for 'mysql_username'") if @mysql_username == nil
    raise("Properties file must contain a value for 'mysql_password'") if @mysql_password == nil
    raise("Properties file must contain a value for 'database_name'") if @database_name == nil

    # Check that we can connect to the webapp.
    begin
      open(@webapp_url) {|f|}
    rescue Exception
      raise "Can't connect to VIVO application at '#{@webapp_url}'"
    end

    # Check that we can connect to the Tomcat manager app.
    begin
      open(@server_url + "manager/list", @tomcat_auth_options) {|f|}
    rescue Exception
      raise "Can't connect to Tomcat manager application at " +
      "'#{@server_url + "manager/list"}', with this authorization: #{@tomcat_auth_options}"
    end

    # Check that we can connect to the MySQL database.
    args = []
    args << "--user=#{@mysql_username}"
    args << "--password=#{@mysql_password}"
    args << "--database=#{@database_name}"
    args << "--version"
    result = system("mysql", *args)
    raise("Can't find the 'mysql' command!") if result == nil
    raise("Can't connect to MySQL database.") if !result
    raise("Error connecting to MySQL database.") if $?.exitstatus != 0
  end

  # The Webapp URL must be a valid URL, and the path must be a single token
  # (with an optional trailing slash).
  #
  def parse_webapp_url(webapp_url)
    match = %r{(\w+://.+/)([^/]+)/?}.match(webapp_url)
    if match
      return [match[1], match[2]]
    else
      raise "Can't parse the webapp name from this URL: '#{webapp_url}'"
    end
  end

  # ------------------------------------------------------------------------------------
  public
  # ------------------------------------------------------------------------------------

  # Get the parameters and check them
  #
  def initialize(properties)
    @webapp_url = properties['webapp_url']
    @tomcat_username = properties['tomcat_username']
    @tomcat_password = properties['tomcat_password']
    @mysql_username = properties['mysql_username']
    @mysql_password = properties['mysql_password']
    @database_name = properties['database_name']

    @server_url, @webapp_name = parse_webapp_url(@webapp_url)
    @tomcat_auth_options = {:http_basic_authentication => [@tomcat_username, @tomcat_password]}

    sanity_checks_on_parameters()
  end
end

#
#
# ------------------------------------------------------------------------------------
# Standalone calling.
# ------------------------------------------------------------------------------------
#
if ARGV.length == 0
  raise("No arguments - usage is: database_cleanser.rb <properties_file>")
end
if !File.file?(ARGV[0])
  raise "File does not exist: '#{ARGV[0]}'."
end

properties = PropertyFileReader.read(ARGV[0])

dc = DatabaseCleanser.new(properties)
