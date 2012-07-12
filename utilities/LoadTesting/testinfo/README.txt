Contains both the JMeter source files for the tests, and the results of the tests.

The source files are stored hierarchically, so we can have multiple sources for each set of tests.
For example, the version of "SimpleTests" that is used with release 1.3 is stored in
  testinfo/tests/SimpleTests/SimpleTests-1-3.jmx

That way _selectTest can determine what sets we have by the names of the subdirectories in tests,
and _runTest can use a distro-dependent version of the test set.

The results are stored in files that correspond the tests, and categorized by the site that 
they run against, so if we run SimpleTests against cornell data on distro of release 1.3, 
the results are stroed in
  results/cornell/SimpleTests-1-3.jtl
  results/cornell/SimpleTests-1-3.html
  results/cornell/SimpleTests-1-3.log
