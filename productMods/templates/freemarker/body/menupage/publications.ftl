<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<p>This body is from the the template file 
   vivo/productMods/templates/freemarker/body/menupage/publications.ftl.  
   In the display model, the publications page has a display:requiresBodyTemplate
   property that defines that the publications page overrides the default template. 
   The default template for these pages is at /vitro/webapp/web/templates/freemarker/body/menupage/menupage.ftl  </p>
   
<p> This technique could be used to define pages without menu items, that get 
    their content from a freemarker template.  An example would be the about page.</p>
    
<code>
display:About <br>
    a display:Page ; <br>
    display:requiresBodyTemplate "about.ftl" ; <br>
    display:title "About" ;<br>
    display:urlMapping "/about" .<br>
    <br>
</code>    

<p>This would create a page that would use about.ftl as the body.  The page would be 
accessed via /about and would override all servlet mappings in web.xml</p>

    