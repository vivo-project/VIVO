<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<@widget name="login" include="assets" />
<#include "browse-classgroups.ftl">

<!DOCTYPE html>
<html lang="en">
    <head>
        <#include "head.ftl">
        <link rel="stylesheet" type="text/css" href="${urls.theme}/css/wilma-iu.css" media="screen"/> 
    </head>
    
    <body class="${bodyClasses!}">
    
        <#include "identity.ftl">
        
        <#include "menu.ftl">
        
            <section id="intro" role="region">
                <h2>Networks and Complex Systems Research at Indiana University</h2>

				<#--                
                <p>VIVO is a research-focused discovery tool that enables collaboration among scientists across all disciplines.</p>
                <p>Browse or search information on people, departments, courses, grants, and publications.</p>
                -->
                 
                <br />
                 
				<ul> This VIVO instance provides information on networks and complex systems
					<li><a href="${urls.base}/people#facultyMember" target="_blank">Faculty</a> and their <a href="${urls.base}/organizations#department" target="_blank">departments</a></li>
					<li><a href="${urls.base}/research#article" target="_blank">Publications</a></li>
					<li><a href="${urls.base}/research#grant" target="_blank">Grants</a></li>
					<li><a href="${urls.base}/individuallist?vclassId=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23Course" target="_blank">Courses</a></li>
				</ul>
				at Indiana University. The site was created in support of a NSF IGERT grant application. A major intent is to cross-fertilize between research done in the social and behavioral sciences, 
				research in natural sciences such as biology or physics, but also research on Internet technologies.
				
				<br />
				<br />	
			
					<ul>The site will be continuously updated to help 
						<li>New faculty to get in contact with relevant researchers.</li>
						<li>Faculty and policy makers to pool teams in response to funding solicitations.</li>
						<li>Faculty to coordinate research efforts  collaborations using existing funding/resources.</li>
						<li>Faculty to coordinate teaching.</li>
						<li>Students identify relevant courses, potential advisors, funding.</li>
						<li>Organize the Mon talk series on <a href="http://vw.slis.indiana.edu/netscitalks/">Networks and Complex Systems.</a></li>
						<li>Arrange research meetings for visitors with relevant faculty/students</li>
						<li>Communicate networks and complex systems research at IU via interlinked web pages to the world.</li>
					</ul>
		
				<br />

				We welcome all comments and suggestions. Please feel free to contact <a href="http://ella.slis.indiana.edu/~katy">Katy Börner</a>
				<a href=mailto:&#107&#97&#116&#121&#64&#105&#110&#100&#105&#97&#110&#97&#46&#101&#100&#117>&#107&#97&#116&#121&#64&#105&#110&#100&#105&#97&#110&#97&#46&#101&#100&#117</a>
				 at the <a href="http://cns.slis.indiana.edu/">Cyberinfrastructure for Network Science Center</a>, SLIS, IUB. Make sure to use “VIVO-NetSci” in the subject header.
                
                
                <#--
                
                <section id="search-home" role="region">
                    <h3>Search VIVO</h3>
                    
                    <fieldset>
                        <legend>Search form</legend>
                        <form id="search-home-vivo" action="${urls.search}" method="post" name="search-home" role="search">
                            <div id="search-home-field">
                                <input type="text" name="querytext" class="search-home-vivo" value="${querytext!}" />
                                <input type="submit" value="Search" class="search">
                            </div>
                        </form>
                    </fieldset>
                </section> <!-- #search-home -->
            </section> <!-- #intro -->
            
            <#--
            
            <@widget name="login" />
            
            -->

			<#assign acceptedClassGroupURIs =[
					"http://vivoweb.org/ontology#vitroClassGrouppeople",
					"http://vivoweb.org/ontology#vitroClassGroupcourses",
					"http://vivoweb.org/ontology#vitroClassGroupevents",
					"http://vivoweb.org/ontology#vitroClassGrouporganizations",
					"http://vivoweb.org/ontology#vitroClassGrouppublications",
					"http://vivoweb.org/ontology#vitroClassGrouppublications"
					]>
            
            <@allClassGroups vClassGroups acceptedClassGroupURIs/>
        
        <#include "footer.ftl">
    </body>
</html>