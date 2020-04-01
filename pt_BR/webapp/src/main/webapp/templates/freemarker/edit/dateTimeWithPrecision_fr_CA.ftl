<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#-- This is an example of how we could call this macro from a custom form if we end up
implementing this like the other freemarker "widgets". For now I just have this here as
an example and I didn't really see any other way to get the jsp custom forms to call
this at the moment. Needless to say, we need to be able to call the macro and pass
parameters from within the original custom form -->

<#--
Available variables:

fieldName -- name of field
minimumPrecision -- minimum precision accepted by validator
requiredLevel -- maximum precision to display as required

existingPrecision -- precision on an existing value, may be ""
year -- year on an existing value, may be ""
month -- month on an existing value, may be ""
day  -- day on an existing value, may be ""
hour -- hour on an existing value, may be ""
minute -- minute on an existing value, may be ""
second -- second on an existing value, may be ""

precisionConstants.none -- URI for precision
precisionConstants.year -- URI for precision
precisionConstants.month -- URI for precision
precisionConstants.day -- URI for precision
precisionConstants.hour -- URI for precision
precisionConstants.minute -- URI for precision
precisionConstants.second -- URI for precision
-->

<@dateTime precision="${minimumPrecision}" required="${requiredLevel}" />

<#macro dateTime precision="full" required=specificity>
    <#assign precLevel = 10 />
    <#assign reqLevel = 10 />
    <#if precision == "${precisionConstants.year}">
        <#assign precLevel = 1 />
    <#elseif precision == "${precisionConstants.month}">
        <#assign precLevel = 2 />
    <#-- allow to specify year, month and day using "date" -->
    <#elseif precision == "${precisionConstants.day}" || precision == "date">
        <#assign precLevel = 3 />
    <#elseif precision == "${precisionConstants.hour}">
        <#assign precLevel = 4 />
    <#-- allow to specify hours and minutes using "time" -->
    <#elseif precision == "${precisionConstants.minute}" || precision == "time">
        <#assign precLevel = 5 />
    <#elseif precision == "${precisionConstants.second}">
        <#assign precLevel = 6 />
    </#if>
    <#if required == "${precisionConstants.year}">
        <#assign reqLevel = 1 />
    <#elseif required == "${precisionConstants.month}">
        <#assign reqLevel = 2 />
    <#-- allow to require year, month and day using "date" -->
    <#elseif required == "${precisionConstants.day}" || required == "date">
        <#assign reqLevel = 3 />
    <#elseif required == "${precisionConstants.hour}">
        <#assign reqLevel = 4 />
    <#-- allow to require hours and minutes using "time" -->
    <#elseif required == "${precisionConstants.minute}" || required == "time">
        <#assign reqLevel = 5 />
    <#elseif required == "${precisionConstants.second}">
        <#assign reqLevel = 6 />
    <#elseif required == "${precisionConstants.none}">
        <#assign reqLevel = 0 />
    </#if>

    <fieldset class="dateTime">

        <#if precLevel gte 1>
            <#-- Only text input field in the mix. We should have some validation to ensure it's a valid year (4 digits, integer, etc) -->
            <label for="${fieldName}-year">
            <#if reqLevel gte 1> <span class="requiredHint">*</span></#if></label>
            <input class="text-field" name="${fieldName}-year" id="${fieldName}-year" type="text" value="${year!}" size="4" maxlength="4" <#if reqLevel gte 1>required </#if>/>
        </#if>

        <#if precLevel gte 2>
            <label for="${fieldName}-month">Month<#if reqLevel gte 2> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}-month" id="${fieldName}-month" <#if reqLevel gte 2>required </#if>>
                <option value=""<#if !month??> selected="selected"</#if>>month</option>
                <#assign numMonths = 12 />
                <#list 1..numMonths as currentMonth>
                    <option value="${currentMonth?string('00')}"<#if month == "${currentMonth}"> selected="selected"</#if>>${currentMonth}</option>
                </#list>
            </select>
        </#if>

        <#if precLevel gte 3>
            <label for="${fieldName}-day">Day<#if reqLevel gte 3> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}-day" id="${fieldName}-day" <#if reqLevel gte 3>required </#if>>
                <option value=""<#if !day??> selected="selected"</#if>>day</option>
                <#assign numDays = 31 />
                <#list 1..numDays as currentDay>
                    <option value="${currentDay?string('00')}"<#if day == "${currentDay}"> selected="selected"</#if>>${currentDay}</option>
                </#list>
            </select>
        </#if>

        <#if precLevel gte 4>
            <#-- We'll need to make this more flexible to support 24 hour display down the road. For now assuming 12h with am/pm -->
            <label for="${fieldName}-hour">Hour<#if reqLevel gte 4> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}-hour" id="${fieldName}-hour" <#if reqLevel gte 3>required </#if>>
                <option value=""<#if !hour??> selected="selected"</#if>>hour</option>
                <#assign numHours = 23 />
                <#list 0..numHours as currentHour>
                    <#if currentHour_index == 0>
                        <#assign displayHour = 12 />
                    <#elseif (currentHour > 12)>
                        <#assign displayHour = currentHour - 12 />
                    <#else>
                        <#assign displayHour = currentHour />
                    </#if>
                    <#if (currentHour <  12)>
                        <#assign meridiem = "am" />
                    <#else>
                        <#assign meridiem = "pm" />
                    </#if>
                    <option value="${currentHour?string('00')}"<#if hour == "${currentHour}"> selected="selected"</#if>>${displayHour + meridiem}</option>
                </#list>
            </select>
        </#if>

        <#if precLevel gte 5>
            <label for="${fieldName}-minute">Minutes<#if reqLevel gte 5> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}-minute" id="${fieldName}-minute" <#if reqLevel gte 5>required </#if>>
                <option value=""<#if !minute??> selected="selected"</#if>>minutes</option>
                <#assign numMinutes = 59 />
                <#list 1..numMinutes as currentMinute>
                    <option value="${currentMinute?string('00')}"<#if minute == "${currentMinute}"> selected="selected"</#if>>${currentMinute}</option>
                </#list>
            </select>
        </#if>

        <#if precLevel gte 6>
            <label for="${fieldName}-second">Seconds<#if reqLevel gte 6> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}-second" id="${fieldName}-second" <#if reqLevel gte 6>required </#if>>
                <option value=""<#if !second??> selected="selected"</#if>>seconds</option>
                <#assign numMinutes = 59 />
                <#list 1..numMinutes as currentSecond>
                    <option value="${currentSecond?string('00')}"<#if second == "${currentSecond}"> selected="selected"</#if>>${currentSecond}</option>
                </#list>
            </select>
        </#if>
    </fieldset>
</#macro>
