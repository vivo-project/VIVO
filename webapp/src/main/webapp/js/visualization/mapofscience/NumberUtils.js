/* $This file is distributed under the terms of the license in /doc/license.txt$ */
// Adapted from http://www.mredkj.com/javascript/numberFormat.html
// format number to a better human readable text
function addCommasToNumber(nStr) {
	nStr += '';
	var x = nStr.split('.');
	var x1 = x[0];
	var x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	
	return x1 + x2;
}