Ruby scripts for analyzing the SPARQL queries that were written to the log by RDFServiceLogger.

Run VIVO, enable logging of SPARQL queries, through the Developer panel, load the page of interest.
Then, run the script to summarize the results.

Run it like this:
./scan_query_times.rb partitions /Users/jeb228/Development/Performance/Weill/tomcat/logs/vivo.all.log overlap unmatched
where:
   "partitions" is the file that defines how the timings will be broken down. Check the "partitions"
        file in this directory for an example
   "......vivo.all.log" is the VIVO log file to be analyzed.
   "overlap" is the file where the queries are written to, if they match more than one criterion.
   "unmatched" is the file where queries are written if they match no criteria.

Read the comments in scan_query_times.rb for more information.

