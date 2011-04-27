<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Macros for datetime formatting 

     In this library, functions are used to format the datetime or interval
     according to a format string and precision, returning a raw string.
     Macros are used to generate the string with appropriate markup.
--> 

<#-- MACROS -->

<#-- Convenience display macros -->
<#macro yearSpan dateTime>
    <#if dateTime?has_content>
        <@dateTimeSpan>${xsdDateTimeToYear(dateTime)}</@dateTimeSpan>
    </#if>
</#macro>

<#macro yearIntervalSpan startDateTime="" endDateTime="" endYearAsRange=true>
    <#local yearInterval = yearInterval(startDateTime, endDateTime, endYearAsRange)>
    <#if yearInterval?has_content>
        <@dateTimeSpan>${yearInterval}</@dateTimeSpan>
    </#if>  
</#macro>

<#-- Display the datetime value or interval in a classed span appropriate for 
     a property statement list -->
<#macro dateTimeSpan>
    <span class="listDateTime"><#nested></span>
</#macro>

<#-- FUNCTIONS -->

<#-- Assign a year precision and generate the interval -->
<#function yearInterval dateTimeStart="" dateTimeEnd="" endYearAsRange=true>
    <#local precision = "yearPrecision">
    <#return dateTimeIntervalShort(dateTimeStart, precision, dateTimeEnd, precision, endYearAsRange)>
</#function>

<#-- Generate a datetime interval with dates displayed as "January 1, 2011" -->
<#function dateTimeIntervalLong dateTimeStart="" precisionStart="" dateTimeEnd="" precisionEnd="" endAsRange=true>
    <#return dateTimeInterval(dateTimeStart, precisionStart, dateTimeEnd, precisionEnd, "long", endAsRange) >
</#function>

<#-- Generate a datetime interval with dates displayed as "1/1/2011" -->
<#function dateTimeIntervalShort dateTimeStart="" precisionStart="" dateTimeEnd="" precisionEnd="" endAsRange=true>
    <#return dateTimeInterval(dateTimeStart, precisionStart, dateTimeEnd, precisionEnd, "short", endAsRange)>
</#function>

<#-- Generate a datetime interval -->
<#function dateTimeInterval dateTimeStart="" precisionStart="" dateTimeEnd="" precisionEnd="" formatType="short" endAsRange=true>

    <#if dateTimeStart?has_content>   
        <#local start = formatXsdDateTime(dateTimeStart, precisionStart, formatType)>
    </#if>
    
    <#if dateTimeEnd?has_content>
        <#local end = formatXsdDateTime(dateTimeEnd, precisionEnd, formatType)>
    </#if>
    
    <#local interval>
        <#if start?? && end??>
            <#if start == end>
                ${start}
            <#else>
                ${start}&nbsp;-&nbsp;${end}
            </#if>
        <#elseif start??>
            ${start} -
        <#elseif end??>
            <#if endAsRange>-&nbsp;</#if>${end}
        </#if>
    </#local>
    
    <#return interval?trim>
</#function>

<#-- Functions for formatting and applying precision to a datetime

     Currently these do more than format the datetime string, they select the precision as well. This should change in a future
     implementation; see NIHVIVO-1567. We want the Java code to apply the precision to the datetime string to pass only the
     meaningful data to the templates. The templates can format as they like, so these functions/macros would do display formatting
     but not data extraction.
     
     On the other hand, this is so easy that it may not be worth re-implementing to gain a bit more MVC compliance.
-->

<#-- Generate a datetime with date formatted as "January 1, 2011" -->
<#function formatXsdDateTimeLong dateTime precision>
    <#return formatXsdDateTime(dateTime, precision, "long")>
</#function>

<#-- Generate a datetime with date formatted as "1/1/2011" -->
<#function formatXsdDateTimeShort dateTime precision>
    <#return formatXsdDateTime(dateTime, precision, "short")>
</#function>

<#-- Generate a datetime as a year -->
<#function xsdDateTimeToYear dateTime>
    <#local precision = "yearPrecision">
    <#return formatXsdDateTime(dateTime, precision)>
</#function>

<#-- Apply a precision and format type to format a datetime -->
<#function formatXsdDateTime dateTimeStr precision="" formatType="short">

    <#-- First convert the string to a format that Freemarker can interpret as a datetime.
         For now, strip away time zone rather than displaying it. -->
    <#local dateTimeStr = dateTimeStr?replace("T", " ")?replace("Z.*$", "", "r")?trim>

    <#-- If a non-standard datetime format (e.g, "2000-04" from
         "2000-04"^^<http://www.w3.org/2001/XMLSchema#gYearMonth>), just
         return the string without attempting to format. Possibly this should
         be handled in Java by examining the xsd type and making an appropriate
         conversion. -->
         
    
    <#if dateTimeStr?matches("(\\d{4})-(\\d{2})-(\\d{2})")>
    	<#-- Convert the string to a datetime object. -->
    	<#local dateTimeObj = dateTimeStr?datetime("yyyy-MM-dd")>
    	     
    <#elseif dateTimeStr?matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})")>
	    <#-- Convert the string to a datetime object. -->
	    <#local dateTimeObj = dateTimeStr?datetime("yyyy-MM-dd HH:mm:ss")>
	    
    <#else>
    	<#return dateTimeStr>
    </#if>
    
    <#-- If no precision is specified, assign it from the datetime value.
         Pass dateTimeStr rather than dateTimeObj, because dateTimeObj
         replaces zeroes with default values, whereas we want to set 
         precision based on whether the times values are all 0. -->
    <#if ! precision?has_content>
        <#local precision = getPrecision(dateTimeStr)>
    </#if>
    
    <#-- Get the format string for the datetime output -->
    <#local format = getFormat(formatType, precision)>

    <#return dateTimeObj?string(format)>
</#function>

<#function getPrecision dateTime>

    <#-- We know this will match because the format has already been checked -->
    <#local match = dateTime?matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2}):(\\d{2})")>

    <#list match as m>
        <#local hours = m?groups[4]?number>
        <#local minutes = m?groups[5]?number>
        <#local seconds = m?groups[6]?number> 
    </#list> 
    
    <#local precision>   
        <#if hours == 0 && minutes == 0 && seconds == 0>yearMonthDayPrecision
        <#else>yearMonthDayTimePrecision
        </#if>
    </#local> 
    
    <#return precision?trim>  
</#function>

<#function getFormat formatType precision>
    <#-- Use the precision to determine which portion to display, 
         and the format type to determine how to display it.  -->    
    <#local format>
        <#if formatType == "long">
            <#if precision == "yearPrecision">yyyy
            <#elseif precision == "yearMonthPrecision">MMMM yyyy
            <#elseif precision == "yearMonthDayPrecision">MMMM d, yyyy
            <#else>MMMM d, yyyy h:mm a
            </#if>
        <#else> <#-- formatType == "short" -->
            <#if precision == "yearPrecision">yyyy
            <#elseif precision == "yearMonthPrecision">M/yyyy
            <#elseif precision == "yearMonthDayPrecision">M/d/yyyy
            <#else>M/d/yyyy h:mm a
            </#if>
        </#if>
    </#local>
    
    <#return format?trim>
</#function>
  

