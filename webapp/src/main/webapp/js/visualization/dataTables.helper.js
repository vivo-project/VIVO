/* $This file is distributed under the terms of the license in /doc/license.txt$ */
$.extend(this, i18nStrings);

var disciplineOrSubdisciplineDataTableFilter = function(oSettings, aData, iDataIndex) {

	/*
	 * We are not showing the first column which holds the info on whether that row contains info on
	 * discipline OR subdiscipline. 
	 * */
	
	if (aData[0] === ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER) {
		return true; 
	} else {
		return false;
	}
}	
	
$.fn.dataTableExt.oPagination.gmail_style = {
    		
        "fnInit": function (oSettings, nPaging, fnCallbackDraw) {
            //var nInfo = document.createElement( 'div' );
            var nFirst = document.createElement('span');
            var nPrevious = document.createElement('span');
            var nNext = document.createElement('span');
            var nLast = document.createElement('span');

            nFirst.innerHTML = "<span class='small-arrows'>&laquo;</span> <span class='paginate-nav-text'>" 
                                + i18nStrings.firstString + "</span>";
            nPrevious.innerHTML = "<span class='small-arrows'>&lsaquo;</span> <span class='paginate-nav-text'>" 
                                + i18nStrings.previousString + "</span>";
            nNext.innerHTML = "<span class='paginate-nav-text'>" 
                                + i18nStrings.nextString + "</span><span class='small-arrows'>&rsaquo;</span>";
            nLast.innerHTML = "<span class='paginate-nav-text'>" 
                                + i18nStrings.lastString + "</span><span class='small-arrows'>&raquo;</span>";

            var oClasses = oSettings.oClasses;
            nFirst.className = oClasses.sPageButton + " " + oClasses.sPageFirst;
            nPrevious.className = oClasses.sPageButton + " " + oClasses.sPagePrevious;
            nNext.className = oClasses.sPageButton + " " + oClasses.sPageNext;
            nLast.className = oClasses.sPageButton + " " + oClasses.sPageLast;

            //nPaging.appendChild( nInfo );
            nPaging.appendChild(nFirst);
            nPaging.appendChild(nPrevious);
            nPaging.appendChild(nNext);
            nPaging.appendChild(nLast);

            $(nFirst).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "first")) {
                    fnCallbackDraw(oSettings);
                }
            });

            $(nPrevious).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "previous")) {
                    fnCallbackDraw(oSettings);
                }
            });

            $(nNext).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "next")) {
                    fnCallbackDraw(oSettings);
                }
            });

            $(nLast).click(function () {
                if (oSettings.oApi._fnPageChange(oSettings, "last")) {
                    fnCallbackDraw(oSettings);
                }
            });

            /* Take the brutal approach to cancelling text selection */
            $('span', nPaging).bind('mousedown', function () {
                return false;
            }).bind('selectstart', function () {
                return false;
            });

            /* ID the first elements only */
            if (oSettings.sTableId !== '' && typeof oSettings.aanFeatures.p == "undefined") {
                nPaging.setAttribute('id', oSettings.sTableId + '_paginate');
                nFirst.setAttribute('id', oSettings.sTableId + '_first');
                nPrevious.setAttribute('id', oSettings.sTableId + '_previous');
                //nInfo.setAttribute( 'id', 'infoContainer' );
                nNext.setAttribute('id', oSettings.sTableId + '_next');
                nLast.setAttribute('id', oSettings.sTableId + '_last');
            }
        },

/*
		 * Function: oPagination.full_numbers.fnUpdate
		 * Purpose:  Update the list of page buttons shows
		 * Returns:  -
 		 * Inputs:   object:oSettings - dataTables settings object
		 *           function:fnCallbackDraw - draw function to call on page change
		 */
        "fnUpdate": function (oSettings, fnCallbackDraw) {
            if (!oSettings.aanFeatures.p) {
                return;
            }

            var iPageCount = 5;
            var iPageCountHalf = Math.floor(iPageCount / 2);
            var iPages = Math.ceil((oSettings.fnRecordsDisplay()) / oSettings._iDisplayLength);
            var iCurrentPage = Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength) + 1;
            var iStartButton, iEndButton, i, iLen;
            var oClasses = oSettings.oClasses;
            
            /* Pages calculation */
            if (iPages < iPageCount) {
                iStartButton = 1;
                iEndButton = iPages;
            } else {
                if (iCurrentPage <= iPageCountHalf) {
                    iStartButton = 1;
                    iEndButton = iPageCount;
                } else {
                    if (iCurrentPage >= (iPages - iPageCountHalf)) {
                        iStartButton = iPages - iPageCount + 1;
                        iEndButton = iPages;
                    } else {
                        iStartButton = iCurrentPage - Math.ceil(iPageCount / 2) + 1;
                        iEndButton = iStartButton + iPageCount - 1;
                    }
                }
            }

            /* Loop over each instance of the pager */
            var an = oSettings.aanFeatures.p;
            var anButtons, anStatic, nPaginateList;
            var fnClick = function () { /* Use the information in the element to jump to the required page */
                var iTarget = (this.innerHTML * 1) - 1;
                oSettings._iDisplayStart = iTarget * oSettings._iDisplayLength;
                fnCallbackDraw(oSettings);
                return false;
            };
            var fnFalse = function () {
                return false;
            };

            for (i = 0, iLen = an.length; i < iLen; i++) {
                if (an[i].childNodes.length === 0) {
                    continue;
                }

                /* Update the 'premanent botton's classes */
                anButtons = an[i].getElementsByTagName('span');
                anStatic = [
                anButtons[0], anButtons[1], anButtons[anButtons.length - 2], anButtons[anButtons.length - 1]];
                $(anStatic).removeClass(oClasses.sPageButton + " " + oClasses.sPageButtonActive + " " + oClasses.sPageButtonStaticDisabled);
                if (iCurrentPage == 1) {
                    anStatic[0].className += " " + oClasses.sPageButtonStaticDisabled;
                    anStatic[1].className += " " + oClasses.sPageButtonStaticDisabled;
                } else {
                    anStatic[0].className += " " + oClasses.sPageButton;
                    anStatic[1].className += " " + oClasses.sPageButton;
                }

                if (iPages === 0 || iCurrentPage == iPages || oSettings._iDisplayLength == -1) {
                    anStatic[2].className += " " + oClasses.sPageButtonStaticDisabled;
                    anStatic[3].className += " " + oClasses.sPageButtonStaticDisabled;
                } else {
                    anStatic[2].className += " " + oClasses.sPageButton;
                    anStatic[3].className += " " + oClasses.sPageButton;
                }
            }
            
            if (typeof GMAIL_STYLE_PAGINATION_CONTAINER_CLASS === 'undefined') {
            	GMAIL_STYLE_PAGINATION_CONTAINER_CLASS = 'paginatedtabs';
            }
            
            if (iPages <= 1) {
            	$("." + GMAIL_STYLE_PAGINATION_CONTAINER_CLASS).hide();
            } else {
            	$("." + GMAIL_STYLE_PAGINATION_CONTAINER_CLASS).show();
            }
        }
    };