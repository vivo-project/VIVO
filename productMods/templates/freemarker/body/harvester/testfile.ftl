<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<script type="text/javascript">

	var harvestProgressResponse;
	function doFunStuff()
	{
		doHarvest();
	}
	
	function doHarvest()
	{
		var request = createRequest();
		request.onreadystatechange=function() {
			if(request.readyState == 4 && request.status == 200) {
				harvestProgressResponse = request.responseText;
				window.setTimeout(continueHarvest, 1000);
			}
		}
		request.open("POST", "/vivo/harvester/testfile", true);
		request.setRequestHeader("content-type","application/x-www-form-urlencoded");
		request.send("${paramIsHarvestClick}=true");
	}
	
	
	function continueHarvest()
	{
		var response = harvestProgressResponse;
		var json = eval("(" + response + ")");

		if(!json.finished) {
			var logAppend = json.progressSinceLastCheck;
			var progressTextArea = document.getElementById("progressTextArea");
			progressTextArea.innerHTML = progressTextArea.innerHTML + logAppend;
			progressTextArea.scrollTop = progressTextArea.scrollHeight;
			
			var request = createRequest();
			request.onreadystatechange=function() {
				if(request.readyState == 4 && request.status == 200) {
					harvestProgressResponse = request.responseText;
					window.setTimeout(continueHarvest, 1000);
				}
			}
			request.open("POST", "/vivo/harvester/testfile", true);
			request.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			request.send("${paramIsHarvestClick}=false");
		}
	}
	


	function createRequest() {
	    var request;
	    if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}
		return request;
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

	function fileResponse()
	{
		var response = frames["uploadTarget"].document.getElementsByTagName("body")[0].innerHTML;
		var json = eval("(" + response + ")");
		
		var fileListing = document.getElementById("fileListing")
		var newLi = document.createElement("li");
		
		if(json.success)
			newLi.innerHTML = json.fileName + " <span style=\"color:green\">" + json.errorMessage + "</span>";
		else
			newLi.innerHTML = json.fileName + " <span style=\"color:red\">upload failed: " + json.errorMessage + "</span>";
		fileListing.appendChild(newLi);
		
		document.getElementById("${paramFirstUpload}").value = "false";
		
		//document.getElementById("responseArea").innerHTML = response;
	}


	function init()
	{
		document.getElementById("${paramFirstUpload}").value = "true";
		document.getElementById("fileUploadForm").onsubmit = function()
		{
			document.getElementById("fileUploadForm").target = "uploadTarget";
			document.getElementById("uploadTarget").onload = fileResponse;
		}
	}
	window.onload = init;
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
		<form id="fileUploadForm" method="post" enctype="multipart/form-data" action="/vivo/harvester/testfile">
			<input type="hidden" id="${paramFirstUpload}" name="${paramFirstUpload}" value="true" />
			<input type="file" name="${paramUploadedFile}" />
			<input type="submit" name="submit" value="Upload" />
			<iframe id="uploadTarget" name="uploadTarget" src="" style="width:0;height:0;border:0px solid #fff;"></iframe>
		</form>
		<h5>Uploaded files</h5>
		<ul id="fileListing">
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
		<div id="progress">
			<textarea cols="100" rows="50" readonly="readonly" id="progressTextArea"></textarea>		
		</div>
	</div>
	<div class="clearBothDiv" />
</div>


