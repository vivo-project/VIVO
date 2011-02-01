<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<div id="deprecated" class="container" role="alert">
   <h1>The vivo-basic theme has been deprecated with the 1.2 release and is not recommended for production instances.</h1>

   <p>Since vivo-basic was the default theme for all previous releases, it is included as part of VIVO 1.2 to help with the transition of upgrading existing installations to latest code, but all vivo-basic development has ceased and it will not be distributed in future releases.</p>

   <p>Please note that vivo-basic does not support all of the new 1.2 features. Most notably, in choosing to use vivo-basic you will be missing out on the following:</p>

   <ul>
       <li>new primary menu for site navigation (replaces tabs)</li>
       <li>home page with class group browse and visual graph</li>
       <li>menu pages with class group browse, individual browse and visual graph</li>
   </ul>

   <p>The new default theme shipped with the application is called <strong>wilma</strong> and fully supports all 1.2 features. For details on how to copy the wilma theme and customize it to your liking, please review the <a href="http://www.vivoweb.org/support/user-guide/administration" title="Download VIVO documentation" target=""_blank>Site Administrator's Guide</a>. You can select your active theme on the site information page, located at <em>Site Admin > Site Information</em>.</p>

   <p><strong>To remove this notification simply comment out the include for vivo-basic-deprecation.ftl in themes/vivo/templates/identity.ftl.</strong></p>
</div>