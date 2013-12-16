<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if !(user.loggedIn && user.hasSiteAdminAccess)>

    <p>${i18n().must_be_admin}</p>

<#else>

    <#if jobKnown == "false">
        
        <p>${i18n().error_no_job_specified}</p>
        
        <p>${i18n().probably_a_bug_so_report}</p>
        
    <#else>
        
        <h2><a class="ingestMenu" href="${urls.base}/ingest" title="${i18n().return_to_ingest_menu}">${i18n().ingest_menu}</a> > ${jobSpecificHeader}</h2>
        
        <#-- check to ensure harvester.location is set in runtime.properties -->
        <#if harvesterLocation?has_content>
            
            <script type="text/javascript">
                
                var harvestProgressResponse;
                
                function doHarvest() {
                    document.getElementById("harvestButton").disabled = true;
                    document.getElementById("harvestButtonHelpText").innerHTML = "${i18n().data_being_harvested}";
                    
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
                    if(checkForFatalError(json)) {
                        return;
                    }
                    
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
                        
                        document.getElementById("harvestButtonHelpText").innerHTML = "${i18n().harvest_complete}";
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
                    if(checkForFatalError(json)) {
                        return;
                    }
                    
                    var fileListing = document.getElementById("fileListing")
                    var newLi = document.createElement("li");
                    
                    if (json.success) {
                        newLi.innerHTML = json.fileName + " <span style=\"color:green\">" + json.errorMessage + "</span>";
                    } else {
                        newLi.innerHTML = json.fileName + " <span style=\"color:red\">upload failed: " + json.errorMessage + "</span>";
                    }
                    
                    fileListing.appendChild(newLi);
                    
                    document.getElementById("${paramFirstUpload}").value = "false";
                    
                    //document.getElementById("responseArea").innerHTML = response;
                }
                
                function init() {
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
                    $('#fileHarvestErrorHelp').removeClass('hidden');
                    $('#fileHarvestForm').addClass('hidden');
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
            
            <div id="fileHarvestErrorHelp" class="hidden">
                <p>${i18n().error_harvest_cannot_continue}</p>
                
                <p>${i18n().harvest_error_instructions_one}</p>
                
                <ol>
                    <li>${i18n().harvest_error_instructions_two}</li>
                    <li>${i18n().the_capitalized} <em>${i18n().harvester_location}</em> ${i18n().harvest_error_instructions_three}</li>
                    <li>${i18n().harvest_error_instructions_fourA}<em>${i18n().harvest_error_instructions_fourB}</em> ${i18n().harvest_error_instructions_fourC}</li>
                    <li>${i18n().harvest_error_instructions_fiveA} <em>${i18n().harvest_error_instructions_fiveB}</em> ${i18n().harvest_error_instructions_fiveC}</li>
                    <li>${i18n().harvest_error_instructions_sixA} <em>${i18n().harvest_error_instructions_sixB}</em> ${i18n().harvest_error_instructions_sixC}</li>
                </ol>
            </div>
            
            <div id="fileHarvestForm">
                <div id="step1" class="testfile-step">
                    <h3 class="testfile-step-header">${i18n().step_one}</h3>
                    <div id="step1-inner" class="testfile-step-body">
                        <h4 class="testfile-step-subheader">${i18n().download_template}</h4>
                        <form id="downloadTemplateForm" method="post" action=${postTo}>
                            <input type="hidden" id="${paramMode}" name="${paramMode}" value="${modeDownloadTemplate}" />
                            <p><input id="submit" type="submit" name="submit" value="${i18n().download}" /> ${jobSpecificDownloadHelp}</p>
                        </form>
                    </div>
                </div>
                
                <div id="step2" class="testfile-step">
                    <h3 class="testfile-step-header">${i18n().step_two}</h3>
                    <div id="step2-inner" class="testfile-step-body">
                        <h4 class="testfile-step-subheader">${i18n().fill_in_data} <a class="help" href="#">${i18n().help_capitalized}</a></h4>
                        <div id="csvHelp-collapsible" class="hidden">
                            <div id="csvHelp-indented">
                                ${jobSpecificFillInHelp}
                            </div>
                        </div>
                        <p>${i18n().fill_in_template_with_data}</p>
                        <div id="csvHelp">
                        </div>
                    </div>
                </div>
                
                <div id="step3" class="testfile-step">
                    <h3 class="testfile-step-header">${i18n().step_three}</h3>
                    <div id="step3-inner" class="testfile-step-body">
                        <h4 class="testfile-step-subheader">${i18n().upload_files}</h4>
                        <p>${i18n().upload_completed_templates}</p>
                        <form id="fileUploadForm" method="post" enctype="multipart/form-data" action=${postTo}>
                            <input type="hidden" id="${paramFirstUpload}" name="${paramFirstUpload}" value="true" />
                            <!--<input type="hidden" id="${paramJob}" name="${paramJob}" value="${job}" /> -->
                            <input type="file" name="${paramUploadedFile}" />
                            <input type="submit" name="submit" value="${i18n().upload_capitalized}" />
                            <iframe id="uploadTarget" name="uploadTarget" src=""></iframe>
                        </form>
                        <h5>${i18n().uploaded_files}</h5>
                        <ul id="fileListing">
                        </ul>
                    </div>
                </div>
                
                <div id="step4" class="testfile-step">
                    <h3 class="testfile-step-header">${i18n().step_four}</h3>
                    <div id="step4-inner" class="testfile-step-body">
                        <h4 class="testfile-step-subheader">${i18n().harvest_capitalized}</h4>
                        <p><input type="button" name="harvestButton" id="harvestButton" class="green button" value="${i18n().harvest_capitalized}" /><span id="harvestButtonHelpText">${i18n().click_to_harvest}</span></p>
                    </div>
                </div>
                
                <div id="step5" class="testfile-step">
                    <h3 class="testfile-step-header">${i18n().step_five}</h3>
                    <div id="step5-inner" class="testfile-step-body">
                        <h4 class="testfile-step-subheader">${i18n().view_results}</h4>
                        <div id="script">
                            <h5>${i18n().script_executed}</h5>
                            <textarea cols="100" rows="20" readonly="readonly" id="scriptTextArea"></textarea>      
                        </div>
                        <div id="progress">
                            <h5>${i18n().progress_capitalized}</h5>
                            <textarea cols="100" rows="20" readonly="readonly" id="progressTextArea"></textarea>        
                        </div>
                        <div id="summary">
                            <h5 id="linkHeader" class="hidden">${jobSpecificLinkHeader}</h5>
                            <ul id="importedItems">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            
        <#else>
            
            <div id="fileHarvestErrorHelp">
                <p>${i18n().the_capitalized} <em>${i18n().harvester_location}</em> ${i18n().undefined_runtime_property}</p>
                
                <p>${i18n().define_value_for_property}</p>
            </div>
            
        <#-- if harvester.location is defined -->
        </#if>
        
        ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/harvester/fileharvest.css" />')}
        
    <#-- if job known -->
    </#if>

<#-- if user is logged-in with site admin access -->
</#if>