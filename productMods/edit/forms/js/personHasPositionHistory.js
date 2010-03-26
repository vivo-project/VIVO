/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var personHasPositionHistory = {
    
    onLoad: function() {
        $("#newOrg").hide();
        $("#position").hide(); 
        $("#submit").val("Continue");
    }
};

$(document).ready(function(){   
    personHasPositionHistory.onLoad();
});
