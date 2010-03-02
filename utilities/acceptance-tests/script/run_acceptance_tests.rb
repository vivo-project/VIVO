=begin
--------------------------------------------------------------------------------

Run the acceptance tests, and summarize their output.

--------------------------------------------------------------------------------

Parameters:
    -- the root directory for the tests. Probably something ending in 
         vivoweb/utilities/acceptance-tests/suites
    -- the directory for the output.
    -- the directory for user-extensions.js
    -- the directory of the Firefox profile template.
    -- the URL of the web site under test.

--------------------------------------------------------------------------------

What we doing?
-- For each subdirectory in the suites folder:
  -- Find the Suite.html 
    -- If none, throw warning into the log and continue.
  -- Run the suite, sending the output to a file named after the subdirectory
    -- If failure, throw error into the log and continue.
-- Create a summary output file.
     result:  passed | failed         -- "and" of all suites.
     total time:   seconds            -- capture the time before and after.
     number of tests:                 -- sum from all suites.
     number of passing tests:         -- sum from all suites.
     number of failing tests:         -- sum from all suites.
     
     Table of links to the suite output files.
--------------------------------------------------------------------------------
=end

<h1>Test suite results </h1>
<table>
<tr><td>result:</td><td>passed</td></tr>
<tr><td>totalTime:</td><td>12</td></tr>
<tr><td>numTestTotal:</td><td>6</td></tr>
<tr><td>numTestPasses:</td><td>6</td></tr>
<tr><td>numTestFailures:</td><td>0</td></tr>
<tr><td>numCommandPasses:</td><td>71</td></tr>
<tr><td>numCommandFailures:</td><td>0</td></tr>
<tr><td>numCommandErrors:</td><td>0</td></tr>
<tr><td>Selenium Version:</td><td>2.0</td></tr>
<tr><td>Selenium Revision:</td><td>a1</td></tr>
<tr><td>
  <table id="suiteTable" class="selenium" border="1" cellpadding="1" cellspacing="1"><tbody>
  <tr class="title status_passed"><td><b>Test Suite</b></td></tr>
  <tr class="  status_passed"><td><a href="#testresult0">Create New User</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult1">First Time Login</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult2">Try New Password</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult3">Edit User Info</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult4">Confirm Edited Info</a></td></tr>
  <tr class="  status_passed"><td><a href="#testresult5">Delete New User</a></td></tr>
  </tbody></table>
  </td><td>&nbsp;</td></tr>
</table>