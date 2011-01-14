<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<@widget name="login" include="assets" />
                       
<!DOCTYPE html>
<html lang="en">
    <head>
        <#include "head.ftl">
        <script type="text/javascript" src="${themeDir}/js/jquery_plugins/raphael/raphael.js"></script>
        <script type="text/javascript" src="${themeDir}/js/jquery_plugins/raphael/pie.js"></script>
    </head>
    
    <body>
        <#include "identity.ftl">
            
        <#include "menu.ftl">
        
            <section id="intro" role="region">
            <h2>Welcome to VIVO</h2>
            
            <p>VIVO is a research-focused discovery tool that enables collaboration among scientists across all disciplines.
			<br /><br />Browse or search information on people, departments, courses, grants, and publications.</p>

                <section id="search-home" role="region">
                    <h3>Search VIVO</h3>
                    
                    <fieldset>
                        <legend>Search form</legend>
                        <form id="search-home-vivo" action="${urls.search}" method="post" name="search-home" role="search">
                            <#if user.showFlag1SearchField>
                                <select id="search-form-modifier" name="flag1" class="form-item" >
                                    <option value="nofiltering" selected="selected">entire database (${user.loginName})</option>
                                    <option value="${portalId}">${siteTagline!}</option>
                                </select>
                            <#else>
                                <input type="hidden" name="flag1" value="${portalId}" />  
                           </#if> 
                            <div id="search-home-field">
                                <input type="text" name="querytext" class="search-home-vivo" value="${querytext!}" />
                                <input type="submit" value="Search" class="submit">
                            </div>
                        </form>
                    </fieldset>
                </section> <!-- #search-home -->
            </section> <!-- #intro -->
          
            <@widget name="login" />
            
            <@widget name="browse" />

            <section id="browse" role="region">
                <h4>Browse</h4>
                
                <ul id="browse-classgroups" role="list">
                    <li role="listitem"><a  class="selected" href="#">People<span class="count-classes"> (1,280)</span></a></li>
                    <li role="listitem"><a href="#">Courses<span class="count-classes"> (1,300)</span></a></li>
                    <li role="listitem"><a href="#">Activities<span class="count-classes"> (980)</span></a></li>
                    <li role="listitem"><a href="#">Topics<span class="count-classes"> (345)</span></a></li>
                    <li role="listitem"><a href="#">Events<span class="count-classes"> (670)</span></a></li>
                    <li role="listitem"><a href="#">Organizations<span class="count-classes"> (440)</span></a></li>
                    <li role="listitem"><a href="#">Publications<span class="count-classes"> (670)</span></a></li>
                    <li role="listitem"><a href="#">Locations<span class="count-classes"> (903)</span></a></li>
                </ul>
                
                <section id="browse-classes" role="navigation">
                    <nav>
                        <ul id="classgroup-list" role="list">
                            <li role="listitem"><a href="#">Faculty Member<span class="count-individuals"> (4,611)</span></a></li>
                            <li role="listitem"><a href="#">Graduate Student<span class="count-individuals"> (2,550)</span></a></li>
                            <li role="listitem"><a href="#">Librarian <span class="count-individuals"> (425)</span></a></li>
                            <li role="listitem"><a href="#">Non-Academic <span class="count-individuals"> (580)</span></a></li>
                            <li role="listitem"><a href="#">Non-Faculty Academic <span class="count-individuals"> (2,380)</span></a></li>
                            <li role="listitem"><a href="#">Postdoc <span class="count-individuals"> (923)</span></a></li>
                            <li role="listitem"><a href="#">Professor Emeritus<span class="count-individuals"> (430)</span></a></li>
                            <li role="listitem"><a class="selected" href="#">Undergraduate Student<span class="count-individuals"> (12,356)</span></a></li>
                        </ul>
                    </nav>
                    
                    <section id="visual-graph" role="region">
                        <h4>Visual Graph</h4>
                        
                        <table>
                            <tr>
                                <th>Faculty Member</th>
                                <td>19%</td>
                            </tr>
                            <tr>
                                  <th>Graduate Student</th>
                                  <td>10%</td>
                              </tr>
                              <tr>
                                  <th>Librarian</th>
                                  <td>2%</td>
                              </tr>
                              <tr>
                                  <th>Non-Academic</th>
                                  <td>3%</td>
                              </tr>
                              <tr>
                                  <th>Non-Faculty Academic</th>
                                  <td>9%</td>
                              </tr>

                              <tr>
                                  <th>Postdoc</th>
                                  <td>4%</td>
                              </tr>
                              <tr>
                                  <th>Professor Emeritus</th>
                                  <td>2%</td>
                              </tr>
                              <tr>
                                  <th>Undergraduate Student</th>
                                  <td>51%</td>
                              </tr>
                          </table>
                          
                          <section id="pieViz" role="region"></section>
                    </section>
                </section> <!-- #browse-classes -->
            </section> <!-- #browse -->
        
        <#include "footer.ftl">
    </body>
</html>
