<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for the body of the Home page -->


<section id="intro">
     <h3>What is VIVO?</h3>
     <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus adipiscing ipsum et ligula accumsan aliquam. Vestibulum posuere mollis arcu quis condimentum. Sed rhoncus nibh vitae lectus mattis accumsan. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam elementum eleifend ante vel aliquam. Fusce non pellentesque leo. Nunc rhoncus vehicula metus, a pellentesque velit elementum ibh vitae lectus mattis accumsan. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam elem. <a href="#">More<span class="pictos-arrow-14"> 4</span></a></p>
     <section id="search-home">
       <h3>Search VIVO</h3>
       <fieldset>
         <legend>Search form</legend>
         <form id="search-home-vivo" action="#" method="post" name="search">
           <div id="search-home-field">
             <input name="search-home-vivo" class="search-home-vivo" id="search-home-vivo"  type="text" />
             <a class ="submit" href="#">Search</a> </div>
         </form>
       </fieldset>
     </section>
   </section>
   <!-- #intro -->
   <section id="log-in">
     <h3>Log in</h3>
     <form id="log-in-form" action="#" method="post" name="log-in-form" />
     <label for="email">Email</label>
     <div class="input-field">
       <input name="email" id="email" type="text" required />
     </div>
     <label for="password">Password</label>
     <div class="input-field">
       <input name="password" id="password" type="password" required />
     </div>
     <div id="wrapper-submit-remember-me"> <a class="green button" href="#">Log in</a>
       <div id="remember-me">
         <input class="checkbox-remember-me" name="remember-me" type="checkbox" value="" />
         <label class="label-remember-me"for="remember-me">Remember me</label>
       </div>
     </div>
     <p class="forgot-password"><a href="#">Forgot your password?</a></p>
     </form>
     <div id="request-account"> <a class="blue button" href="#">Request an account</a> </div>
   </section><!-- #log-in -->
   <section id="browse">
     <h2>Browse</h2>
     <ul id="browse-classGroups">
       <li><a  class="selected" href="#">class group 1<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 2<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 3<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 4<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 5<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group 6<span class="count-classes"> (280)</span></a></li>
       <li><a href="#">class group n<span class="count-classes"> (280)</span></a></li>
     </ul>
     <section id="browse-classes">
       <nav>
         <ul id="class-group-list">
           <li><a  class="selected" href="#">class 1<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 2<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 3<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 4<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 5<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 6<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 7<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 8<span class="count-individuals"> (280)</span></a></li>
           <li><a href="#">class 9<span class="count-individuals"> (280)</span></a></li>
         </ul>
       </nav>
       <section id="visual-graph">
         <h4>Visual Graph</h4>
         <img src="images/visual-graph.jpg" /> </section>
     </section>
   </section><!-- Browse -->
   <section id="highlights">
     <h2>Highlights</h2>
     <section id="fearuted-people" class="global-highlights">
       <h3>FEATURED PEOPLE</h3>
       <!--use Hs-->
       <article class="featured-people vcard"> <img  class="individual-photo" src="images/person-thumbnail.jpg" width="80" height="80" />
         <p class="fn">foaf:lastName, foaf:firstName <span class="title">core:preferredTitle</span><span class="org">currentPosition(s)</span></p>
       </article>
       <article class="featured-people vcard"> <img  class="individual-photo" src="images/person-thumbnail.jpg" width="80" height="80" />
         <p class="fn">foaf:lastName, foaf:firstName <span class="title">core:preferredTitle</span><span class="org">currentPosition(s)</span></p>
       </article>
     </section><!-- featured-people -->
     <section id="upcoming-events" class="global-highlights">
       <h3>UPCOMING EVENTS</h3>
       <article class="vevent">
         <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Aug</span></time>
         <p class="summary">individualName. eventClass
           <time>9:30 AM</time>
         </p>
       </article>
       <article class="vevent">
         <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Aug</span></time>
         <p class="summary">individualName. eventClass
           <time>9:30 AM</time>
         </p>
       </article>
       <article class="vevent">
         <time class="dtstart" datetime="2010-02-13T20:00Z">20<span>Aug</span></time>
         <p class="summary">individualName. eventClass
           <time>9:30 AM</time>
         </p>
       </article>
       <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
     </section><!-- upcoming-events -->
     <section id="latest-publications" class="global-highlights">
       <h3>LATEST PUBLICATIONS</h3>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <article class="latest-publications-item">
         <p class="publication-content">individualName<span><em>Audio Content</em>
           <time datetime="2010-02-13T20:00Z"> | March 2010</time>
         </p>
       </article>
       <p class="view-all"><a class="view-all-style" href="#">View All <span class="pictos-arrow-10">4</span></a></p>
     </section><!-- latest-publications -->
   </section>
