<script type="text/javascript">
	
	var alreadyDone = false;
	function doFunStuff()
	{
		if(!alreadyDone)
		{
			alreadyDone = true;
			document.getElementById("progress").style.visibility = "visible"
			document.getElementById("progressUploading").style.visibility = "visible"
			window.setTimeout(uploadingSuccess, 2000);
		}
	}
	function uploadingSuccess()
	{
		document.getElementById("progressUploadingSuccess").style.visibility = "visible"
		document.getElementById("progressGenerating").style.visibility = "visible"
		window.setTimeout(generatingSuccess, 2000);
	}
	function generatingSuccess()
	{
		document.getElementById("progressGeneratingSuccess").style.visibility = "visible"
		document.getElementById("progressExecuting").style.visibility = "visible"
		window.setTimeout(executingSuccess, 2000);
	}
	function executingSuccess()
	{
		document.getElementById("progressExecutingSuccess").style.visibility = "visible"
		document.getElementById("results").style.visibility = "visible"
	}

	function toggleCollapsibleDiv(divName)
	{
		var display = document.getElementById(divName).style.display;
		if(display == "none")
			document.getElementById(divName).style.display = "inline";
		else
			document.getElementById(divName).style.display = "none";
	}

	function toggleHelp()
	{
		toggleCollapsibleDiv("help-collapsible");
	}

	function toggleCsvHelp()
	{
		toggleCollapsibleDiv("csvHelp-collapsible");
	}

</script>


<style>
	h3.testfile-step-header
	{
		float:left;
		padding-right:30px
	}
	h4.testfile-step-subheader
	{
		
	}
	div.testfile-step-body
	{
		overflow:hidden
	}
	div.clearBothDiv
	{
		clear:both;
		padding-bottom:20px
	}
</style>



<div id="step1" class="testfile-step">
	<h3 class="testfile-step-header">Step 1</h3>
	<div id="step1-inner" class="testfile-step-body">
		<h4 class="testfile-step-subheader">Download template</h4>
		<p><input type="button" value="Download" style="margin-right:10px" />We are providing a helpful template file for you to download.</p>
	</div>
	<div class="clearBothDiv" />
</div>
<div id="step2" class="testfile-step">
	<h3 class="testfile-step-header">Step 2</h3>
	<div id="step2-inner" class="testfile-step-body">
		<h4 class="testfile-step-subheader">Fill in data <a style="font-size:smaller;margin-left:10px" onclick="toggleCsvHelp();return false;" href="#">Help</a></h4>
		<div id="csvHelp-collapsible" style="display:none">
			<div id="csvHelp-indented" style="margin-left:20px;font-size:smaller">
				<p>A CSV, or <b>C</b>omma-<b>S</b>eparated <b>V</b>alues file, is a method of storing tabular data in plain text.  The first line of a CSV file contains header information, while each subsequent line contains a data record.</p>
				<p>The template we provide contains only the header, which you will then fill in accordingly.  For example, if the template contains the text "firstName,lastName", then you might add two more lines, "John,Doe" and "Jane,Public".</p>
			</div>
		</div>
		<p>Fill in the template with your data.  You may fill in multiple templates if you wish to harvest multiple files at once.</p>
		<div id="csvHelp">
		</div>
	</div>
	<div class="clearBothDiv" />
</div>
<div id="step3" class="testfile-step">
	<h3 class="testfile-step-header">Step 3</h3>
	<div id="step3-inner" class="testfile-step-body">
		<h4 class="testfile-step-subheader">Upload file(s)</h4>
		<p>Upload your filled-in template(s).</p>
		<input type="file" />
		<input type="button" value="Upload" />
		<h5>Uploaded files</h5>
		<ul>
			<li>file1.csv <span style="color:green">success</span></li>
			<li>file2.csv <span style="color:green">success</span></li>
			<li>file3.csv <span style="color:red">upload failed: error parsing CSV file</span></li>
		</ul>
	</div>
	<div class="clearBothDiv" />
</div>
<div id="step4" class="testfile-step">
	<h3 class="testfile-step-header">Step 4</h3>
	<div id="step4-inner" class="testfile-step-body">
		<h4 class="testfile-step-subheader">Harvest <a style="font-size:smaller;margin-left:10px" onclick="toggleHelp();return false;" href="#">Help</a></h4>
		<div id="help-collapsible" style="display:none">
			<div id="help-refworks" style="margin-left:20px;margin-bottom:10px;font-size:smaller">
				<h4>Instructions</h4>
				<h5>Export the file from RefWorks</h5>
				<ul>
					<li>From the <b>References</b> or <b>Tools</b> menu select <b>Export</b>.</li>
					<li>Under <b>Export Format</b> select <b>BibTeX - Ref ID</b>.</li>
					<li>Click the <b>Export to Text File</b> button to view the file and save it to your hard disk.</li>
				</ul>		
				<h5>Import the file into VIVO</h5>
				<ul>
					<li>Drag the file onto the space below and click <b>Import</b>.</li>
				</ul>
			</div>
		</div>
		<p><input type="button" value="Harvest" style="margin-right:10px" onclick="doFunStuff();" />Click the button to harvest your file(s).</p>
	</div>
	<div class="clearBothDiv" />
</div>
<div id="step5" class="testfile-step">
	<h3 class="testfile-step-header">Step 5</h3>
	<div id="step5-inner" class="testfile-step-body">
		<h4 class="testfile-step-subheader">View results</h4>
		<div id="progress" style="visibility:hidden">
			<ul>
				<li id="progressUploading" style="visibility:hidden">Validating files... <span id="progressUploadingSuccess" style="visibility:hidden">success</span></li>
				<li id="progressGenerating" style="visibility:hidden">Generating script... <span id="progressGeneratingSuccess" style="visibility:hidden">success</span></li>
				<li id="progressExecuting" style="visibility:hidden">Executing script... <span id="progressExecutingSuccess" style="visibility:hidden">success</span></li>
			</ul>
		</div>
		<div style="height:30px"></div>
		<div id="results" style="visibility:hidden">
			<h3>Results</h3>
			<span style="color:green;font-weight:bold">Harvest successful.</span>
			<h4>Script used for harvest</h4>
			<div><input type="button" value="Save" style="margin-bottom:10px" /></div>
			<textarea cols="100" rows="50" readonly="readonly"><#noparse>#!/bin/bash

# Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams, Michael Barbieri.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the new BSD license
# which accompanies this distribution, and is available at
# http://www.opensource.org/licenses/bsd-license.html
# 
# Contributors:
#     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams, Michael Barbieri - initial API and implementation

#KNOWN ISSUE: Seems to tie in matches that were originally in VIVO into the input model, so that if the input model is cleaned out of VIVO,
#             then those matches will be removed.  Actually they remain, hidden, but much of their data including their rdf:type is gone.  An
#             RDF export will show this.

# Exit on first error
set -e

# Set working directory
HARVESTERDIR=`dirname "$(cd "${0%/*}" 2>/dev/null; echo "$PWD"/"${0##*/}")"`
HARVESTERDIR=$(cd $HARVESTERDIR; cd ..; pwd)

HARVESTER_TASK=mods

if [ -f scripts/env ]; then
  . scripts/env
else
  exit 1
fi
echo "Full Logging in $HARVESTER_TASK_DATE.log"

BASEDIR=harvested-data/$HARVESTER_TASK
BIBINDIR=$BASEDIR/rh-bibutils-in
BIBOUTDIR=$BASEDIR/rh-bibutils-out
RAWRHDIR=$BASEDIR/rh-raw
RAWRHDBURL=jdbc:h2:$RAWRHDIR/store
RDFRHDIR=$BASEDIR/rh-rdf
RDFRHDBURL=jdbc:h2:$RDFRHDIR/store
MODELDIR=$BASEDIR/model
MODELDBURL=jdbc:h2:$MODELDIR/store
MODELNAME=modsTempTransfer
SCOREDATADIR=$BASEDIR/score-data
SCOREDATADBURL=jdbc:h2:$SCOREDATADIR/store
SCOREDATANAME=modsScoreData
TEMPCOPYDIR=$BASEDIR/temp-copy
MATCHEDDIR=$BASEDIR/matched
MATCHEDDBURL=jdbc:h2:$MATCHEDDIR/store
MATCHEDNAME=matchedData

#scoring algorithms
EQTEST="org.vivoweb.harvester.score.algorithm.EqualityTest"
LEVDIFF="org.vivoweb.harvester.score.algorithm.NormalizedLevenshteinDifference"

#matching properties
CWEMAIL="http://vivoweb.org/ontology/core#workEmail"
SWEMAIL="http://vivoweb.org/ontology/score#workEmail"
FFNAME="http://xmlns.com/foaf/0.1/firstName"
SFNAME="http://vivoweb.org/ontology/score#foreName"
FLNAME="http://xmlns.com/foaf/0.1/lastName"
CMNAME="http://vivoweb.org/ontology/core#middleName"
BPMID="http://purl.org/ontology/bibo/pmid"
CTITLE="http://vivoweb.org/ontology/core#title"
BISSN="http://purl.org/ontology/bibo/ISSN"
PVENUEFOR="http://vivoweb.org/ontology/core#publicationVenueFor"
LINKAUTH="http://vivoweb.org/ontology/core#linkedAuthor"
LINKINFORES="http://vivoweb.org/ontology/core#linkedInformationResource"
AUTHINAUTH="http://vivoweb.org/ontology/core#authorInAuthorship"
RDFTYPE="http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
RDFSLABEL="http://www.w3.org/2000/01/rdf-schema#label"
BASEURI="http://vivoweb.org/harvest/mods/"

#BIBUTILSBASE="lib/bibutils/bibutils_4.12_x86_64"
BIBUTILSBASE="lib/bibutils/bibutils_4.12_i386"
BIBUTILSINPUTFORMAT="med"



#clear old fetches
rm -rf $BIBINDIR

# Execute Fetch for Pubmed
$PubmedFetch -X config/tasks/ufl.pubmedfetch.xml -o $TFRH -OfileDir=$BIBINDIR

# clear old bibutils runs
rm -rf $BIBOUTDIR

# run bibutils
$RunBibutils -b $BIBUTILSBASE -m $BIBUTILSINPUTFORMAT -i $TFRH -IfileDir=$BIBINDIR -o $TFRH -OfileDir=$BIBOUTDIR

# clear old sanitizes
rm -rf $RAWRHDIR

# Sanitize data
$SanitizeMODSXML -i $TFRH -IfileDir=$BIBOUTDIR -o $TFRH -OfileDir=$RAWRHDIR

# clear old translates
rm -rf $RDFRHDIR

# Execute Translate using the mods-to-vivo.xsl file
$XSLTranslator -i $TFRH -IfileDir=$RAWRHDIR -o $H2RH -OdbUrl=$RDFRHDBURL -x config/datamaps/mods-to-vivo.xsl -f

# backup translate
BACKRDF="rdf"
backup-path $RDFRHDIR $BACKRDF
# uncomment to restore previous translate
#restore-path $RDFRHDIR $BACKRDF

# Clear old H2 transfer model
rm -rf $MODELDIR

# Execute Transfer to import from record handler into local temp model
$Transfer -o $H2MODEL -OmodelName=$MODELNAME -OcheckEmpty=$CHECKEMPTY -OdbUrl=$MODELDBURL -h $H2RH -HdbUrl=$RDFRHDBURL

# backup H2 transfer Model
BACKMODEL="model"
backup-path $MODELDIR $BACKMODEL
# uncomment to restore previous H2 transfer Model
#restore-path $MODELDIR $BACKMODEL

# Clear old H2 score data
rm -rf $SCOREDATADIR

# Clear old H2 match data
rm -rf $MATCHEDDIR

# Clear old H2 temp copy
rm -rf $TEMPCOPYDIR

# Score variables for cleaner lines
SCOREINPUT="-i $H2MODEL -ImodelName=$MODELNAME -IdbUrl=$MODELDBURL -IcheckEmpty=$CHECKEMPTY"
SCOREDATA="-s $H2MODEL -SmodelName=$SCOREDATANAME -SdbUrl=$SCOREDATADBURL -ScheckEmpty=$CHECKEMPTY"
MATCHOUTPUT="-o $H2MODEL -OmodelName=$MATCHEDNAME -OdbUrl=$MATCHEDDBURL -OcheckEmpty=$CHECKEMPTY"
MATCHEDINPUT="-i $H2MODEL -ImodelName=$MATCHEDNAME -IdbUrl=$MATCHEDDBURL -IcheckEmpty=$CHECKEMPTY"
SCOREMODELS="$SCOREINPUT -v $VIVOCONFIG -VcheckEmpty=$CHECKEMPTY $SCOREDATA -t $TEMPCOPYDIR -b $SCOREBATCHSIZE"

# Execute Score to disambiguate data in "scoring" JENA model
TITLE="-Atitle=$EQTEST -Ftitle=$RDFSLABEL -Wtitle=1.0 -Ptitle=$RDFSLABEL"

$Score $SCOREMODELS $TITLE -n  ${BASEURI}pub/
$Match $SCOREINPUT $SCOREDATA -t 0.7 -r

# clear H2 score data Model
rm -rf $SCOREDATADIR


#Author, Organization, Geographic Location, Journal match
LNAME="-AlName=$LEVDIFF -FlName=$FLNAME -WlName=0.5 -PlName=$FLNAME"
FNAME="-AfName=$LEVDIFF -FfName=$FFNAME -WfName=0.3 -PfName=$FFNAME"
RDFSLABELSCORE="-ArdfsLabel=$LEVDIFF -FrdfsLabel=$RDFSLABEL -WrdfsLabel=1.0 -PrdfsLabel=$RDFSLABEL"

$Score $SCOREMODELS $FNAME $LNAME -n ${BASEURI}author/
$Score $SCOREMODELS $RDFSLABELSCORE -n ${BASEURI}org/
$Score $SCOREMODELS $RDFSLABELSCORE -n ${BASEURI}geo/
$Score $SCOREMODELS $RDFSLABELSCORE -n ${BASEURI}journal/
$Match $SCOREINPUT $SCOREDATA -t 0.7 -r


# clear H2 score data Model
rm -rf $SCOREDATADIR

# Clear old H2 temp copy of input (URI here is hardcoded in Score)
$JenaConnect -Jtype=tdb -JdbDir=$TEMPCOPYDIR -JmodelName=http://vivoweb.org/harvester/model/scoring#inputClone -t


#Authorship match
AUTHPUB="-Aauthpub=$EQTEST -Fauthpub=$LINKINFORES -Wauthpub=0.5 -Pauthpub=$LINKINFORES"
AUTHAUTH="-Aauthauth=$EQTEST -Fauthauth=$LINKAUTH -Wauthauth=0.5 -Pauthauth=$LINKAUTH"

$Score $SCOREMODELS $AUTHPUB $AUTHAUTH -n ${BASEURI}authorship/
$Match $SCOREINPUT $SCOREDATA -t 0.7 -r






# backup H2 score data Model
BACKSCOREDATA="scoredata-auths"
backup-path $SCOREDATADIR $BACKSCOREDATA
# uncomment to restore previous H2 matched Model
#restore-path $SCOREDATADIR $BACKSCOREDATA

# clear H2 score data Model
rm -rf $SCOREDATADIR

# Clear old H2 temp copy
rm -rf $TEMPCOPYDIR






# Execute ChangeNamespace lines: the -o flag value is determined by the XSLT used to translate the data
CNFLAGS="$SCOREINPUT -v $VIVOCONFIG -VcheckEmpty=$CHECKEMPTY -n $NAMESPACE"
# Execute ChangeNamespace to get unmatched Publications into current namespace
$ChangeNamespace $CNFLAGS -u ${BASEURI}pub/
# Execute ChangeNamespace to get unmatched Authorships into current namespace
$ChangeNamespace $CNFLAGS -u ${BASEURI}authorship/
# Execute ChangeNamespace to get unmatched Authors into current namespace
$ChangeNamespace $CNFLAGS -u ${BASEURI}author/
# Execute ChangeNamespace to get unmatched Organizations into current namespace
$ChangeNamespace $CNFLAGS -u ${BASEURI}org/
# Execute ChangeNamespace to get unmatched Geographic Locations into current namespace
$ChangeNamespace $CNFLAGS -u ${BASEURI}geo/
# Execute ChangeNamespace to get unmatched Journals into current namespace
$ChangeNamespace $CNFLAGS -u ${BASEURI}journal/


# Backup pretransfer vivo database, symlink latest to latest.sql
BACKPREDB="pretransfer"
backup-mysqldb $BACKPREDB
# uncomment to restore pretransfer vivo database
#restore-mysqldb $BACKPREDB

PREVHARVESTMODEL="http://vivoweb.org/ingest/mods"
ADDFILE="$BASEDIR/additions.rdf.xml"
SUBFILE="$BASEDIR/subtractions.rdf.xml"

# Find Subtractions
$Diff -m $VIVOCONFIG -MmodelName=$PREVHARVESTMODEL -McheckEmpty=$CHECKEMPTY -s $H2MODEL -ScheckEmpty=$CHECKEMPTY -SdbUrl=$MODELDBURL -SmodelName=$MODELNAME -d $SUBFILE
# Find Additions
$Diff -m $H2MODEL -McheckEmpty=$CHECKEMPTY -MdbUrl=$MODELDBURL -MmodelName=$MODELNAME -s $VIVOCONFIG -ScheckEmpty=$CHECKEMPTY -SmodelName=$PREVHARVESTMODEL -d $ADDFILE

PREVHARVESTMODELINPUT="-i $VIVOCONFIG -ImodelName=$PREVHARVESTMODEL -IcheckEmpty=$CHECKEMPTY"


# Backup adds and subs
backup-file $ADDFILE adds.rdf.xml
backup-file $SUBFILE subs.rdf.xml

# Apply Subtractions to Previous model
$Transfer -o $H2MODEL -OdbUrl=${PREVHARVDBURLBASE}${HARVESTER_TASK}/store -OcheckEmpty=$CHECKEMPTY -OmodelName=$PREVHARVESTMODEL -r $SUBFILE -m
# Apply Additions to Previous model
$Transfer -o $H2MODEL -OdbUrl=${PREVHARVDBURLBASE}${HARVESTER_TASK}/store -OcheckEmpty=$CHECKEMPTY -OmodelName=$PREVHARVESTMODEL -r $ADDFILE
# Apply Subtractions to VIVO
$Transfer -o $VIVOCONFIG -OcheckEmpty=$CHECKEMPTY -r $SUBFILE -m
# Apply Additions to VIVO
$Transfer -o $VIVOCONFIG -OcheckEmpty=$CHECKEMPTY -r $ADDFILE


#Restart Tomcat
#Tomcat must be restarted in order for the harvested data to appear in VIVO
echo $HARVESTER_TASK ' completed successfully'
/etc/init.d/tomcat stop
/etc/init.d/apache2 reload
/etc/init.d/tomcat start</#noparse></textarea>
		</div>
	</div>
	<div class="clearBothDiv" />
</div>

