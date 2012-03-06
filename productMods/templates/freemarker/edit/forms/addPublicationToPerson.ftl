<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a publication to a foaf:Persons -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />


<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />

<#--assign htmlForElements = editConfiguration.pageData.htmlForElements ! {}/-->

<#--drop down options for a field are included in page data with that field name-->
<#assign pubTypeLiteralOptions = editConfiguration.pageData.pubType />
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#--In case of submission error, may already have publication type or title - although latter not likely, but storing values to be on safe side-->
<#assign publicationTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "pubType") />
<#assign titleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "title") />
<#assign pubUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "pubUri") />
<#assign collectionValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "collection") />
<#assign collectionUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "collectionUri") />
<#assign bookValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "book") />
<#assign bookUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "bookUri") />
<#assign conferenceValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conference") />
<#assign conferenceUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conferenceUri") />
<#assign eventValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "event") />
<#assign eventUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "eventUri") />
<#assign editorValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "editor") />
<#assign editorUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "editorUri") />
<#assign publisherValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "publisher") />
<#assign publisherUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "publisherUri") />
<#assign localeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "locale") />
<#assign volumeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "volume") />
<#assign numberValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "number") />
<#assign issueValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "issue") />
<#assign startPageValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "startPage") />
<#assign endPageValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "endPage") />

<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Save Changes">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Entry">
        <#assign disabledVal=""/>
</#if>

<h2>${titleVerb}&nbsp;publication entry for ${editConfiguration.subjectName}</h2>

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	${submissionErrors[errorFieldName]} <br/>
        </#list>
        
        </p>
    </section>
</#if>


<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#if editMode = "error">
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>

<section id="addPublicationToPerson" role="region">        
    
<@lvf.unsupportedBrowser urls.base/>
<form id="addpublicationToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit publication">
        
        <#--TODO: Check if possible to have existing publication options here in order to select-->
    <p class="inline"><label for="typeSelector">Publication Type<#if editMode != "edit"> ${requiredHint}<#else>:</#if></label>
        <select id="typeSelector" name="pubType" acGroupName="publication" >
             <option value="" <#if (publicationTypeValue?length = 0)>selected="selected"</#if>>Select one</option>
             <#list pubTypeLiteralOptions?keys as key>
                 <option value="${key}" <#if (publicationTypeValue = key)>selected="selected"</#if>>${pubTypeLiteralOptions[key]}</option>
             </#list>
        </select>
    </p>
    <div class="fullViewOnly">        
        <p>
            <label for="title">Title ${requiredHint}</label>
            <input class="acSelector" size="60"  type="text" id="title" name="title" acGroupName="publication"  value="${titleValue}" />
        </p>

        <div class="acSelection" acGroupName="publication" id="pubAcSelection">
            <p class="inline">
                <label>Selected Publication:</label>
                <span class="acSelectionInfo"></span>
                <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
                <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
            </p>
            <input class="acUriReceiver" type="hidden" id="pubUri" name="pubUri" value="${pubUriValue}" />
        </div>
    <div id="fieldsForNewPub">
    <#-- Published In: collection -->
    <p>
        <label for="collection">Published in</label>
        <input class="acSelector" size="50"  type="text" id="collection" name="collection" acGroupName="collection"  value="${collectionValue}" />
    </p>

    <div class="acSelection" acGroupName="collection" >
        <p class="inline">
            <label>Selected Venue:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="collectionUri" name="collectionUri" value="${collectionUriValue}" />
    </div>

    <#-- Published In: book -->
    <p>
        <label for="book">Published in</label>
        <input class="acSelector" size="50"  type="text" id="book" name="book" acGroupName="book"  value="${bookValue}" />
    </p>

    <div class="acSelection" acGroupName="book" >
        <p class="inline">
            <label>Selected Venue:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="bookUri" name="bookUri" value="${bookUriValue}" />
    </div>

    <#-- Presented At -->
    <p>
        <label for="conference">Presented at</label>
        <input class="acSelector" size="50"  type="text" id="conference" name="conference" acGroupName="conference"  value="${conferenceValue}" />
    </p>

    <div class="acSelection" acGroupName="conference" >
        <p class="inline">
            <label>Selected Venue:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="conferenceUri" name="conferenceUri" value="${conferenceUriValue}" />
    </div>
        
    <#-- Proceedings of -->
    <p>
        <label for="event">Proceedings of</label>
        <input class="acSelector" size="50"  type="text" id="event" name="event" acGroupName="event"  value="${eventValue}" />
    </p>

    <div class="acSelection" acGroupName="event" >
        <p class="inline">
            <label>Selected Organization:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="eventUri" name="eventUri" value="${eventUriValue}" />
    </div>

    <#-- Editor -->
    <p>
        <label for="editor">Editor</label>
        <input class="acSelector" size="50"  type="text" id="editor" name="editor" acGroupName="editor"  value="${editorValue}" />
    </p>

    <div class="acSelection" acGroupName="editor" >
        <p class="inline">
            <label>Selected Editor:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="editorUri" name="editorUri" value="${editorUriValue}" />
    </div>

    <#-- Publisher -->
    <p>
        <label for="publisher">Publisher</label>
        <input class="acSelector" size="50"  type="text" id="publisher" name="publisher" acGroupName="publisher"  value="${publisherValue}" />
    </p>

    <div class="acSelection" acGroupName="publisher" >
        <p class="inline">
            <label>Selected Publisher:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="publisherUri" name="publisherUri" value="${publisherUriValue}" />
    </div>
    
    <#-- Place of Publication -->
    <p>
        <label for="locale">Place of Publication</label>
        <input  size="30"  type="text" id="locale" name="locale" acGroupName="locale"  value="${localeValue}" />
    </p>
    
    <#-- Volume, Number and Issue -->
    <p class="inline">
        <label for="volume" id="volLabel">Volume</label>
        <label for="number" id="nbrLabel" class="vniLabels">Number</label>
        <label for="issue" id="issueLabel" class="vniLabels">Issue</label>
    </p>
    <p>
        <input  size="4" type="text" id="volume" name="volume" value="${volumeValue}" />
        <input  size="4" class="vniInputs" type="text" id="number" name="number" value="${numberValue}" />
        <input  size="4" class="vniInputs" type="text" id="issue" name="issue" value="${issueValue}" />
    </p>

    <#-- Start/End Pages -->
    <p class="inline">
        <label for="startPage" id="sPLabel">Start Page</label>
        <label for="endPage" class="sepLabels">End Page</label>
    </p>
    <p>
        <input  size="4" type="text" id="startPage" name="startPage" value="${startPageValue}" />
        <input  size="4" class="sepInputs" type="text" id="endPage" name="endPage" value="${endPageValue}" />
    </p>

    <#-- Publication Date -->
    <p>
    <#assign htmlForElements = editConfiguration.pageData.htmlForElements />
    <#if htmlForElements?keys?seq_contains("dateTime")>
        <label class="dateTime" for="pubDate">Publication Date</label><p></p>
		${htmlForElements["dateTime"]} ${yearHint}
    </#if>
    </p>
    </div> <!-- end fieldsForNewPub -->

   </div> <!-- end fullViewOnly -->
       <p class="submit">
            <input type="hidden" name = "editKey" value="${editKey}"/>
            <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span><a class="cancel" href="${cancelUrl}">Cancel</a>
       </p>

       <p id="requiredLegend" class="requiredHint">* required fields</p>
    </form>


<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >

    <script type="text/javascript">
    var customFormData  = {
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        acUrl: '${urls.base}/autocomplete?tokenize=true',
        acTypes: {collection: 'http://purl.org/ontology/bibo/Periodical', book: 'http://purl.org/ontology/bibo/Book', conference: 'http://purl.org/NET/c4dm/event.owl#Event', event: 'http://purl.org/NET/c4dm/event.owl#Event', editor: 'http://xmlns.com/foaf/0.1/Person', publisher: 'http://xmlns.com/foaf/0.1/Organization'},
        editMode: '${editMode}',
        defaultTypeName: 'publication', // used in repair mode to generate button text
        multipleTypeNames: {collection: 'publication', book: 'book', conference: 'conference', event: 'event', editor: 'editor', publisher: 'publisher'},
        baseHref: '${urls.base}/individual?uri='
    };
    </script>
    
    <script type="text/javascript">
     $(document).ready(function(){
        publicationToPersonUtils.onLoad();
    }); 
    </script>
</section>
</#if>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/publicationToPersonUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}
