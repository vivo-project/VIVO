<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if !(user.loggedIn && user.hasSiteAdminAccess)>

    <p>You must be an administrator to use this tool.</p>

<#else>

    <#if jobKnown == "false">
        
        <p>Error: No file harvest job was specified, or an unknown job was specified.</p>
        
        <p>The end user should not see this error under normal circumstances, so this is probably a bug and should be reported.</p>
        
    <#else>
    
        <script type="text/javascript">

            var harvestProgressResponse;

            function doHarvest() {
                document.getElementById("harvestButton").disabled = true;
                document.getElementById("harvestButtonHelpText").innerHTML = "Please wait while your data is harvested.";

                var request = createRequest();
                request.onreadystatechange=function() {
                    if(request.readyState == 4 && request.status == 200) {
                        harvestProgressResponse = request.responseText;

                        var json = eval("(" + harvestProgressResponse + ")");
                        if(checkForFatalError(json))
                            return;
                        
                        var scriptTextArea = document.getElementById("scriptTextArea");
                        scriptTextArea.innerHTML = json.scriptText;

                        window.setTimeout(continueHarvest, 1000);
                    }
                }
                request.open("POST", "${postTo}", true);
                request.setRequestHeader("content-type","application/x-www-form-urlencoded");
                //request.send("${paramMode}=${modeHarvest}&${paramJob}=${job}");
                request.send("${paramMode}=${modeHarvest}");
            }


            function continueHarvest() {
                var response = harvestProgressResponse;
                var json = eval("(" + response + ")");
                if(checkForFatalError(json))
                    return;

                var logAppend = json.progressSinceLastCheck;
                var progressTextArea = document.getElementById("progressTextArea");
                progressTextArea.innerHTML = progressTextArea.innerHTML + logAppend;
                progressTextArea.scrollTop = progressTextArea.scrollHeight;

                if(!json.finished) {
                    var request = createRequest();
                    request.onreadystatechange=function() {
                        if(request.readyState == 4 && request.status == 200) {
                            harvestProgressResponse = request.responseText;
                            window.setTimeout(continueHarvest, 1000);
                        }
                    }
                    request.open("POST", "${postTo}", true);
                    request.setRequestHeader("Content-type","application/x-www-form-urlencoded");
                    //request.send("${paramMode}=${modeCheckStatus}&${paramJob}=${job}");
                    request.send("${paramMode}=${modeCheckStatus}");
                } else {

                    // var linkHeader = document.getElementById("linkHeader");
                    // linkHeader.style.display = "inline";
                    $('#linkHeader').removeClass('hidden');

                    var importedItems = document.getElementById("importedItems")

                    if(json.newlyAddedUrls.length > 0) {
                        for(var i = 0; i < json.newlyAddedUrls.length; i++) {

                            var newLi = document.createElement("li");
                            newLi.innerHTML = "<a href=\"" + json.newlyAddedUrls[i] + "\" target=\"_blank\">" + json.newlyAddedUris[i] + "</a>";
                            importedItems.appendChild(newLi);
                        }
                    } else {

                        var newLi = document.createElement("li");
                        newLi.innerHTML = "${jobSpecificNoNewDataMessage}";
                        importedItems.appendChild(newLi);
                    }

                    document.getElementById("harvestButtonHelpText").innerHTML = "Harvest complete.  For another, please refresh the page.";
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

            function fileResponse() {
                var response = frames["uploadTarget"].document.getElementsByTagName("body")[0].innerHTML;
                var json = eval("(" + response + ")");
                if(checkForFatalError(json))
                    return;

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

            function init() {
                <#if harvesterPathError == "true">
                	handleFatalError();
                <#else>
	                document.getElementById("harvestButton").disabled = false;
	
	                document.getElementById("${paramFirstUpload}").value = "true";
	                document.getElementById("fileUploadForm").onsubmit = function()
	                {
	                    document.getElementById("fileUploadForm").target = "uploadTarget";
	                    document.getElementById("uploadTarget").onload = fileResponse;
	                }
	                document.getElementById("downloadTemplateForm").onsubmit = function()
	                {
	                    document.getElementById("downloadTemplateForm").target = "uploadTarget";
	                }
                </#if>
            }
            
            function checkForFatalError(json) {
                if(json.fatalError) {
                	handleFatalError();
                    return true;
                } else {
                    return false;
                }
            }
            
            function handleFatalError() {
                document.getElementById("fileHarvestErrorHelp").style.display = "inline";
                document.getElementById("fileHarvestForm").style.display = "none";
            } 
            
            
            
            window.onload = init;
            
            $(document).ready(function() {
                $('a.help').click(function() {
                    $('#csvHelp-collapsible').toggleClass('hidden');
                    return false;
                });
                
                $('#harvestButton').click(function() {
                    doHarvest();
                    return false;
                });
            });

        </script>
        
		<div id="fileHarvestErrorHelp">
			<p>An error has occurred and the file harvest cannot continue.</p>
			<p>This is most likely due to an improper Harvester configuration.  Please ensure the following:</p>
			<ul>
				<li>VIVO Harvester is installed.</li>
				<li>The harvester.location setting in deploy.properties pointed to the Harvester installation directory.</li>
				<li>In VIVO Harvester, the web server user (typically tomcat6) has read and write access to the vivo/ directory and all of its children.</li>
				<li>In VIVO Harvester, the logs/ directory exists and the web server user has read and write access to it.</li>
				<li>In VIVO Harvester, the file vivo/config/vivo.xml is properly configured with your database information and namespace.</li>
			</ul>
		</div>

		<div id="fileHarvestForm">
	        <h2><a class="ingestMenu" href="${urls.base}/ingest">Ingest Menu</a> > ${jobSpecificHeader}</h2>

	        <div id="step1" class="testfile-step">
	            <h3 class="testfile-step-header">Step 1</h3>
	            <div id="step1-inner" class="testfile-step-body">
	                <h4 class="testfile-step-subheader">Download template</h4>
	                <form id="downloadTemplateForm" method="post" action=${postTo}>
	                    <input type="hidden" id="${paramMode}" name="${paramMode}" value="${modeDownloadTemplate}" />
	                    <p><input id="submit" type="submit" name="submit" value="Download" /> ${jobSpecificDownloadHelp}</p>
	                </form>
	            </div>
	            <div class="clearBothDiv" />
	        </div>
	        
	        <div id="step2" class="testfile-step">
	            <h3 class="testfile-step-header">Step 2</h3>
	            <div id="step2-inner" class="testfile-step-body">
	                <h4 class="testfile-step-subheader">Fill in data <a class="help" href="#">Help</a></h4>
	                <div id="csvHelp-collapsible" class="hidden">
	                    <div id="csvHelp-indented">
	                        ${jobSpecificFillInHelp}
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
	                <form id="fileUploadForm" method="post" enctype="multipart/form-data" action=${postTo}>
	                    <input type="hidden" id="${paramFirstUpload}" name="${paramFirstUpload}" value="true" />
	                    <!--<input type="hidden" id="${paramJob}" name="${paramJob}" value="${job}" /> -->
	                    <input type="file" name="${paramUploadedFile}" />
	                    <input type="submit" name="submit" value="Upload" />
	                    <iframe id="uploadTarget" name="uploadTarget" src=""></iframe>
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
	                <h4 class="testfile-step-subheader">Harvest</h4>
	                <p><input type="button" name="harvestButton" id="harvestButton" class="green button" value="Harvest" /><span id="harvestButtonHelpText">Click the button to harvest your file(s).</span></p>
	            </div>
	            <div class="clearBothDiv" />
	        </div>
	        
	        <div id="step5" class="testfile-step">
	            <h3 class="testfile-step-header">Step 5</h3>
	            <div id="step5-inner" class="testfile-step-body">
	                <h4 class="testfile-step-subheader">View results</h4>
	                <div id="script">
	                    <h5>Script being executed</h5>
	                    <textarea cols="100" rows="20" readonly="readonly" id="scriptTextArea"></textarea>      
	                </div>
	                <div id="progress">
	                    <h5>Progress</h5>
	                    <textarea cols="100" rows="20" readonly="readonly" id="progressTextArea"></textarea>        
	                </div>
	                <div id="summary">
	                    <h5 id="linkHeader" class="hidden">${jobSpecificLinkHeader}</h5>
	                    <ul id="importedItems">
	                    </ul>
	                </div>
	            </div>
	            <div class="clearBothDiv" />
	        </div>
		</div>        
        ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/harvester/fileharvest.css" />')}
        
    <#-- if job known -->
    </#if>

<#-- if user is logged-in with site admin access -->
</#if>