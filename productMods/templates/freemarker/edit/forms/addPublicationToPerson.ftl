<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for adding a publication to a foaf:Persons -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />

<#assign newUriSentinel = "" />
<#if editConfigurationConstants?has_content>
	<#assign newUriSentinel = editConfigurationConstants["NEW_URI_SENTINEL"] />
</#if>

<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />

<#--assign htmlForElements = editConfiguration.pageData.htmlForElements ! {}/-->

<#--drop down options for a field are included in page data with that field name-->
<#assign pubTypeLiteralOptions = editConfiguration.pageData.pubType />
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#--The blank sentinel indicates what value should be put in a URI when no autocomplete result has been selected.
If the blank value is non-null or non-empty, n3 editing for an existing object will remove the original relationship
if nothing is selected for that object-->
<#assign blankSentinel = "" />
<#if editConfigurationConstants?has_content && editConfigurationConstants?keys?seq_contains("BLANK_SENTINEL")>
	<#assign blankSentinel = editConfigurationConstants["BLANK_SENTINEL"] />
</#if>

<#--This flag is for clearing the label field on submission for an existing object being selected from autocomplete.
Set this flag on the input acUriReceiver where you would like this behavior to occur. -->
<#assign flagClearLabelForExisting = "flagClearLabelForExisting" />


<#--In case of submission error, may already have publication type or title - although latter not likely, but storing values to be on safe side-->
<#assign publicationTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "pubType") />
<#assign titleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "title") />
<#assign pubUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "pubUri") />
<#assign collectionValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "collection") />
<#assign collectionDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "collectionDisplay") />
<#assign collectionUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "collectionUri") />
<#assign bookValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "book") />
<#assign bookDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "bookDisplay") />
<#assign bookUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "bookUri") />
<#assign conferenceValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conference") />
<#assign conferenceDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conferenceDisplay") />
<#assign conferenceUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "conferenceUri") />
<#assign eventValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "event") />
<#assign eventDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "eventDisplay") />
<#assign eventUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "eventUri") />
<#assign editorValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "editor") />
<#assign editorDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "editorDisplay") />
<#assign editorUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "editorUri") />
<#assign firstNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "firstName") />
<#assign lastNameValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "lastName") />
<#assign publisherValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "publisher") />
<#assign publisherDisplayValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "publisherDisplay") />
<#assign publisherUriValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "publisherUri") />
<#assign localeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "locale") />
<#assign volumeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "volume") />
<#assign numberValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "number") />
<#assign issueValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "issue") />
<#assign chapterNbrValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "chapterNbr") />
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

    <#if collectionDisplayValue?has_content >
        <#assign collectionValue = collectionDisplayValue />
    </#if>
    <#if bookDisplayValue?has_content >
        <#assign bookValue = bookDisplayValue />
    </#if>
    <#if conferenceDisplayValue?has_content >
        <#assign conferenceValue = conferenceDisplayValue />
    </#if>
    <#if eventDisplayValue?has_content >
        <#assign eventValue = eventDisplayValue />
    </#if>
    <#if editorDisplayValue?has_content >
        <#assign editorValue = editorDisplayValue />
    </#if>
    <#if publisherDisplayValue?has_content >
        <#assign publisherValue = publisherDisplayValue />
    </#if>

    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#if lvf.submissionErrorExists(editSubmission, "title")>
 	        Please select an existing publication in the Title field or enter a new one.<br />
        <#else> 
            <#list submissionErrors?keys as errorFieldName>
        	    ${submissionErrors[errorFieldName]} <br/>
            </#list>
        </#if>
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
            <input class="acUriReceiver" type="hidden" id="pubUri" name="pubUri" value="${pubUriValue}"  ${flagClearLabelForExisting}="true" />
        </div>
    <div id="fieldsForNewPub">
    <#-- Published In: collection -->
    <p>
        <label for="collection">Published in</label>
        <input class="acSelector" size="50"  type="text" id="collection" name="collection" acGroupName="collection"  value="${collectionValue}" />
        <input class="display" type="hidden" id="collectionDisplay" name="collectionDisplay" acGroupName="collection"  value="${collectionDisplayValue}" />
    </p>

    <div class="acSelection" acGroupName="collection" >
        <p class="inline">
            <label>Selected Journal:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="collectionUri" name="collectionUri" value="${collectionUriValue}" ${flagClearLabelForExisting}="true" />
    </div>

    <#-- Published In: book -->
    <p>
        <label for="book">Published in</label>
        <input class="acSelector" size="50"  type="text" id="book" name="book" acGroupName="book"  value="${bookValue}" />
        <input class="display" type="hidden"  id="bookDisplay" name="bookDisplay" acGroupName="book"  value="${bookDisplayValue}" />
    </p> 

    <div class="acSelection" acGroupName="book" >
        <p class="inline">
            <label>Selected Book:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="bookUri" name="bookUri" value="${bookUriValue}" ${flagClearLabelForExisting}="true" />
    </div>

    <#-- Presented At -->
    <p>
        <label for="conference">Presented at</label>
        <input class="acSelector" size="50"  type="text" id="conference" name="conference" acGroupName="conference"  value="${conferenceValue}" />
        <input class="display" type="hidden" id="conferenceDisplay" name="conferenceDisplay" acGroupName="conference"  value="${conferenceDisplayValue}" />
    </p>

    <div class="acSelection" acGroupName="conference" >
        <p class="inline">
            <label>Selected Event:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="conferenceUri" name="conferenceUri" value="${conferenceUriValue}" ${flagClearLabelForExisting}="true" />
    </div>
        
    <#-- Proceedings of -->
    <p>
        <label for="event">Proceedings of</label>
        <input class="acSelector" size="50"  type="text" id="event" name="event" acGroupName="event"  value="${eventValue}" />
        <input class="display" type="hidden" id="eventDisplay" name="eventDisplay" acGroupName="event"  value="${eventDisplayValue}" />
    </p>

    <div class="acSelection" acGroupName="event" >
        <p class="inline">
            <label>Selected Organization:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="eventUri" name="eventUri" value="${eventUriValue}" ${flagClearLabelForExisting}="true" />
    </div>


    <#-- Editor -->
    <p>
        <label class="editor" for="editor">Editor: Last Name<span style="padding-left:338px">First Name  ${requiredHint}</span>
            <span class="note">(required with new Last Name)</span>
        </label>
        <input class="acSelector" size="50"  type="text" id="editor" name="editor" acGroupName="editor"  value="${editorValue}" />
        <input  size="30"  type="text" id="firstName" name="firstName" value="${firstNameValue}" ><br />
        <input type="hidden" id="lastName" name="lastName" value="">
        <input class="display" type="hidden" id="editorDisplay" name="editorDisplay" acGroupName="editor"  value="${editorDisplayValue}" />
    </p>

    <div class="acSelection" acGroupName="editor" >
        <p class="inline">
            <label>Selected Editor:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="editorUri" name="editorUri" value="${editorUriValue}" ${flagClearLabelForExisting}="true" />
    </div>

    <#-- Publisher -->
    <p>
        <label for="publisher">Publisher</label>
        <input class="acSelector" size="50"  type="text" id="publisher" name="publisher" acGroupName="publisher"  value="${publisherValue}" />
        <input class="display" type="hidden" id="publisherDisplay" name="publisherDisplay" acGroupName="publisher" value="${publisherDisplayValue}" />
    </p>

    <div class="acSelection" acGroupName="publisher" >
        <p class="inline">
            <label>Selected Publisher:</label>
            <span class="acSelectionInfo"></span>
            <a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
            <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="publisherUri" name="publisherUri" value="${publisherUriValue}" ${flagClearLabelForExisting}="true" />
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
        <label for="issue" id="chapterNbrLabel" class="vniLabels">Chapter</label>
    </p>
    <p>
        <input  size="4" type="text" id="volume" name="volume" value="${volumeValue}" />
        <input  size="4" class="vniInputs" type="text" id="number" name="number" value="${numberValue}" />
        <input  size="4" class="vniInputs" type="text" id="issue" name="issue" value="${issueValue}" />
        <input  size="4" class="vniInputs" type="text" id="chapterNbr" name="chapterNbr" value="${chapterNbrValue}" />
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
        baseHref: '${urls.base}/individual?uri=',
        blankSentinel: '${blankSentinel}',
        flagClearLabelForExisting: '${flagClearLabelForExisting}'
    };
    </script>
    
    <script type="text/javascript">
     $(document).ready(function(){
        publicationToPersonUtils.onLoad('${urls.base}/individual?uri=', '${blankSentinel}');
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
