<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->


<#include "scienceMapSetup.ftl">

<script language="JavaScript" type="text/javascript">

$(document).ready(function() {

var scienceMapDataURL = "${organizationMapOfScienceDataURL}";


    	$.ajax({
            url: scienceMapDataURL
            dataType: "json",
            timeout: 5 * 60 * 1000,
            success: function (data) {
    		
                if (data.error) {

					alert("error");
					alert(data);

                } else {
                	
                	alert("success");
					alert(data);
                    
                }
            }
        });

});

</script>