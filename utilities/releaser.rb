=begin
--------------------------------------------------------------------------------

Create the VIVO distribution files.

This will:
1) Export the desired release from Subversion
2) Copy all files from the export area to the licensed area, 
    adding licensing text in the process.
3) Pack the licensed files into a Zip file and Tar.GZ file for distribution

--------------------------------------------------------------------------------
2010-01-27 initial version J.Blake
--------------------------------------------------------------------------------
=end

# --- Get the name of the working directory. If it doesn't exist, create it. If it does, check that it is empty.
# --- Show the existing tags, and ask the user to pick one (default is alphabetically the highest)
# --- Export from subversion
# --- Add licenses
# --- Pack the licensed files into Zip and GZ

=begin
mkdir release_0_9
cd release_0_9
svn export http://svn.mannlib.cornell.edu/svn/vivoweb/tags/rel-0.9-rc1 export-0.9
== RUN LICENSER ==
zip -r ../rel-0.9-rc1.zip vivo-rel-0.9
tar -czf ../rel-0.9-rc1.tar.gz vivo-rel-0.9
=end

=begin
svn copy http://svn.mannlib.cornell.edu/svn/vivoweb/trunk http://svn.mannlib.cornell.edu/svn/vivoweb/tags/rel-0.9-rc1 -m "Release 0.9 rc1 tag"
svn copy http://svn.mannlib.cornell.edu/svn/vivoweb/trunk http://svn.mannlib.cornell.edu/svn/vivoweb/branches/rel-0.9-maint -m "Release 0.9 maintenance branch"
=end
