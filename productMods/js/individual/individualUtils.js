/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){

    // ensures proper layout when an organization has its webpage link displayed as a thumnail.
    // there's a timing issue, so we can't check the length here, so check the role just to see
    // if $('ul.webpages-withThumnails') exists
    if ( $('ul.webpages-withThumbnails').children('li').length > 0 ) {
        $('div.individual-overview').css("float","left");
        $('div#activeGrantsLink').css("margin-top","30px");
        $('section#individual-info').children('h2#overview').css("clear","both");
    }
    $.extend(this, i18nStrings);

    // "more"/"less" HTML truncator for showing more or less content in data property core:overview
    $('.overview-value').truncate({max_length: 500});
        
    // Change background color button when verbose mode is off
    $('a#verbosePropertySwitch:contains("' + i18nStrings.verboseTurnOff + '")').addClass('verbose-off');
    
    // Reveal vCard QR code when QR icon is clicked
    $('#qrIcon, .qrCloseLink').click(function() {
	
		
		// only create the img the first time, so check if it already exists
		if ( !$('img#codeImage').length ) {		
			$.ajax({
            	url: baseUrl + "/qrCodeAjax",
            	dataType: "json",
            	data: {
                	action: "getQrCodeDetails",
					uri: individualUri,
            	},
            	complete: function(xhr, status) {
                	var results = $.parseJSON(xhr.responseText);
                	if ( results.length == 0 ) {
                    	var html = i18nStrings.currentlyNoResearchers;
                	}
                	else {
						if ( results[0].firstName.length < 1 || results[1].lastName.length < 1 ) {
							$('#qrCodeImage').css("width","225px");
							var noCodeStr = "<div style='padding:25px 0 30px 22px;font-size:13px'>"
							                + "The QR Code could not be generated due to incomplete information about this person. </div>"
							$('#qrCodeImage').prepend(noCodeStr);
						}
						else if ( results[0].firstName.length > 0 || results[1] == null || results[1].lastName.length > 0 ) {
							var vcard = "";
							vcard += "BEGIN:VCARD" + String.fromCharCode(13);
							vcard += "VERSION:3.0" + String.fromCharCode(13);
							vcard += "N:" + results[1].lastName + String.fromCharCode(13);
							vcard += "FN:" + results[0].firstName + String.fromCharCode(13);
							if ( results[2].preferredTitle.length > 0 ) {
								vcard += "TITLE:" + results[2].preferredTitle + String.fromCharCode(13);
							}
							if ( results[3].phoneNumber.length > 0 ) {
								vcard += "TEL;TYPE=WORK,VOICE:" + results[3].phoneNumber + String.fromCharCode(13);
							}
							if ( results[4].email.length > 0 ) {
								vcard += "EMAIL;TYPE=PREF,INTERNET:" + results[4].email + String.fromCharCode(13);
							}
							vcard += "URL:" + individualUri + String.fromCharCode(13);
							if ( individualPhoto.length > 0 ) {
								vcard += "PHOTO;VALUE=URL;TYPE=JPG:" + individualPhoto + String.fromCharCode(13);
							}
							vcard += "END:VCARD";
							
							spanStr = "<a title='${i18n().export_qr_codes}' href='"
							          + exportQrCodeUrl + "'>"
							          + "<img id='codeImage' src='https://chart.googleapis.com/chart?cht=qr&amp;chs=125x125&amp;chl="
									  + vcard 
									  + "&amp;choe=UTF-8'/>"
									  + "</a>";
									
							$('#qrCodeImage').prepend(spanStr);
							$('#qrCodeImage').toggleClass('hidden');
						}
                 	}
            	}
       		});        
        } 
		else { 
			$('#qrCodeImage').toggleClass('hidden');
		}
    });

    // For pubs and grants on the foaf:person profile, and affiliated people
    // on the foaf:organization profile -- if a pub/grant/person has been hidden 
    // via the "manage" link, we need to ensure that the subclass heading gets removed
    // if there are no items to display for that subclass.
    $.each($('h3'), function() {
        if ( $(this).next().attr('class') == "subclass-property-list hideThis" ) {
            if ( $(this).next().children().length == 0 ) {       
                    $(this).closest('li').remove();
            }
        }
    });
        
    // if there are no selected pubs, hide the manage link; same for grants
    // and affiliated people on the org profile page
    if ( $('ul#relatedBy-Authorship-List').children('li').length < 1 && $('h3#relatedBy-Authorship').attr('class') != "hiddenPubs" ) {
        $('a#managePubLink').hide();
    }

    if ( $('ul#RO_0000053-ResearcherRole-List').children('li').length < 1 &&
            $('ul#RO_0000053-PrincipalInvestigatorRole-List').children('li').length < 1 &&
            $('ul#RO_0000053-CoPrincipalInvestigatorRole-List').children('li').length < 1 &&
            $('ul#RO_0000053-InvestigatorRole-List').children('li').length < 1 &&
            $('h3#RO_0000053-ResearcherRole').attr('class') != "hiddenGrants" ) {
                    $('a#manageGrantLink').hide();
    }

    if ( $('ul#relatedBy-Position-List').children('li').length < 1 && $('h3#relatedBy-Position').attr('class') != "hiddenPeople" ) {
        $('a#managePeopleLink').hide();
    }
   
    // if there are webpages but no contacts (email/phone), extend
    // the webpage border the full width. Used with "2 column" profile view.
    if ( $('h2#contactHeading').length < 1 ) {
        if ( $('div#webpagesContainer').length ) {
             $('div#webpagesContainer').css('width', '100%').css('clear','both');
        }
    }    
});
