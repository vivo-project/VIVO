The distros directory contains subdirectories, each with a VIVO distribution, as checked out from Subversion.
These can be read-only checkouts. Examples of such directories might be release1.3, release1.4, trunk, etc.

Each distro directory contains:
   The vivo and vitro workspace areas, as checked out from Subversion
   deploy_properties.template
     This is a deploy properties file suitable for building the distrbution.
     Some property values are placeholders that will be set when the _deploy script is run.
     Examples of placeholders include:
         Vitro.defaultNamespace = <%= @default_namespace %>
         vitro.home.directory = /home/jeb228/LoadTesting/versions/<%= @version_name %>/data
     This means that when _deploy is run, it must have values for the variables
     @default_namespace and @version_name, which it will substitute into the template.
   distro_properties.rb
     A ruby source file which defines some values which apply to the distribution.
     Currently, the only value is @test_suffix.
     For example, release1.4 uses @test_suffix = "-1-4", so if we select "TestA", 
     the _runTest script will look for a file called TestA-1-4.jtl, and run that.

