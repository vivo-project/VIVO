<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for the body of the GadgetDetails page -->

<style media="screen" type="text/css">
th { 
  vertical-align: top;
  width: 150px;
  text-align: right;
  padding-right: 10px;
}
</style>	

<div class="pageTitle" id="gadgets-title"><h2>${title!}</h2></div>

<#-- VIVO OpenSocial Extension by UCSF -->
<#if openSocial??>
   	<form method="POST">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <th>
                    Gadget URLs</br>
                    One Per Line
                </th>
                <td>
                    <textarea name="gadgetURLS" rows="10" cols="160">${gadgetURLS}</textarea>
                </td>
            </tr>
            <tr>
                <th>
                    Debug mode
                </th>
                <td>
                    <input type="checkbox" name="debug" value="debug" checked="checked" />
                </td>
            </tr>
            <tr>
                <th>
                    Use Cache
                </th>
                <td>
                    <input type="checkbox" name="useCache" value="useCache" />
                </td>
            </tr>
            <tr>
                <th>
                </th>
                <td>
                    <input type="submit" value="Submit" />
                </td>
            </tr>
        </table>
   	</form>
</#if>	
