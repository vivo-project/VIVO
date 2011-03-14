/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){
    
    // "more"/"less" HTML truncator for showing more or less content in data property core:overview
    $('.overview-value').truncate({max_length: 500});
    
    // Change background color button when verbose mode is off
    $('#verbosePropertyForm input#submit[value="Turn off"]').addClass("verbose-off")
});