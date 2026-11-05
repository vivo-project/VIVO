/* $This file is distributed under the terms of the license in LICENSE$ */

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

    // The QR code is a non-modal disclosure. Keep its controls exposed while open.
    var qrTrigger = $('#qrIcon');
    var qrPopup = $('#qrCodeImage');
    var qrLoaded = qrPopup.find('img').length > 0;
    var qrLoading = false;

    function setQrOpen(open, moveFocus) {
        if (!open && $.contains(qrPopup[0], document.activeElement)) {
            qrTrigger.trigger('focus');
        }
        qrPopup.toggleClass('hidden', !open).prop('hidden', !open).removeAttr('aria-hidden');
        qrTrigger.attr('aria-expanded', open ? 'true' : 'false');
        if (open && moveFocus) {
            qrPopup.find('.qrCloseLink').first().trigger('focus');
        }
    }

    if (qrTrigger.length && qrPopup.length) {
        qrTrigger.attr('aria-controls', 'qrCodeImage');
        qrTrigger.filter('a').attr('role', 'button');
        setQrOpen(false, false);
    }

    qrTrigger.on('click', function(event) {
        event.preventDefault();
        if (!qrPopup.length) {
            return;
        }
        var open = qrPopup.hasClass('hidden');
        setQrOpen(open, true);
        if (!open || qrLoaded || qrLoading) {
            return;
        }

        qrLoading = true;
        $.ajax({
            url: baseUrl + '/qrCodeAjax',
            dataType: 'json',
            data: { action: 'getQrCodeDetails', uri: individualUri },
            complete: function(xhr, status) {
                qrLoading = false;
                if (status !== 'success') {
                    return;
                }
                var results = xhr.responseJSON || JSON.parse(xhr.responseText);
                var firstName = (results[0] || {}).firstName || '';
                var lastName = (results[1] || {}).lastName || '';
                if (!firstName || !lastName) {
                    qrPopup.css('width', '225px').prepend($('<div/>')
                        .css({ padding: '25px 0 30px 22px', fontSize: '13px' })
                        .text('The QR Code could not be generated due to incomplete information about this person.'));
                    qrLoaded = true;
                    return;
                }

                var vcard = 'BEGIN:VCARD\rVERSION:3.0\rN:' + lastName + '\rFN:' + firstName + '\r';
                if ((results[2] || {}).preferredTitle) {
                    vcard += 'TITLE:' + results[2].preferredTitle + '\r';
                }
                if ((results[3] || {}).phoneNumber) {
                    vcard += 'TEL;TYPE=WORK,VOICE:' + results[3].phoneNumber + '\r';
                }
                if ((results[4] || {}).email) {
                    vcard += 'EMAIL;TYPE=PREF,INTERNET:' + results[4].email + '\r';
                }
                vcard += 'URL:' + individualUri + '\r';
                if (individualPhoto.length > 0) {
                    vcard += 'PHOTO;VALUE=URL;TYPE=JPG:' + individualPhoto + '\r';
                }
                vcard += 'END:VCARD';

                qrPopup.prepend($('<a/>').attr({ title: i18nStrings.exportQrCodes, href: exportQrCodeUrl })
                    .append($('<img/>').attr({
                        id: 'codeImage',
                        alt: qrTrigger.attr('aria-label') || qrTrigger.attr('title') || i18nStrings.exportQrCodes,
                        src: 'https://quickchart.io/chart?cht=qr&chs=125x125&chl=' + encodeURIComponent(vcard) + '&choe=UTF-8'
                    })));
                qrLoaded = true;
                // Do not reopen or move focus if the user closed it while loading.
            }
        });
    });

    qrTrigger.filter('a').on('keydown', function(event) {
        if (event.key === ' ' || event.which === 32) {
            event.preventDefault();
            $(this).trigger('click');
        }
    });
    qrPopup.find('.qrCloseLink').on('click', function(event) {
        event.preventDefault();
        qrTrigger.trigger('focus');
        setQrOpen(false, false);
    });
    qrPopup.add(qrTrigger).on('keydown', function(event) {
        if ((event.key === 'Escape' || event.which === 27) && !qrPopup.hasClass('hidden')) {
            event.preventDefault();
            event.stopPropagation();
            qrTrigger.trigger('focus');
            setQrOpen(false, false);
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
