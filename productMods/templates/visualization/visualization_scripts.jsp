<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%  /***********************************************
    This file is used to inject <link> and <script> elements
    into the head element of the generated source of a VITRO
    page that is being displayed by the entity controller.
    
    In other words, anything like domain.com/entity?...
    will have the lines specified here added in the <head>
    of the page.
    
    This is a great way to specify JavaScript or CSS files
    at the entity display level as opposed to globally.
    
    Example:
    <link rel="stylesheet" type="text/css" href="/css/entity.css"/>" media="screen"/>
    <script type="text/javascript" src="/js/jquery.js"></script>
****************************************************/ %>

<c:url var="jquery" value="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"/>
<c:url var="googleVisualizationAPI" value="http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D"/>

<script type="text/javascript" src="${jquery}"></script>
<script type="text/javascript" src="${googleVisualizationAPI}"></script>
