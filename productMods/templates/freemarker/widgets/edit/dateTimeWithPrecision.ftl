<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--
year: <input name="${fieldName}.year" type="text" value="${year}"/> <br/>
month: <input name="${fieldName}.month" type="text" value="${month}"/> <br/>
day: <input name="${fieldName}.day" type="text" value="${day}"/> <br/>
hour: <input name="${fieldName}.hour" type="text" value="${hour}"/> <br/>
minute: <input name="${fieldName}.minute" type="text" value="${minute}"/> <br/>
second: <input name="${fieldName}.second" type="text" value="${second}"/> <br/>
-->

<#-- This is an example of how we could call this macro from a custom form if we end up
implementing this like the other freemarker "widgets". For now I just have this here as
an example and I didn't really see any other way to get the jsp custom forms to call
this at the moment. Needless to say, we need to be able to call the macro and pass
parameters from within the original custom form -->
<@dateTime specificity="year" required="none" />

<#macro dateTime specificity="full" required=specificity>
    <#assign specLevel = 10 />
    <#assign reqLevel = 10 />
    <#if specificity == "year">
        <#assign specLevel = 1 />
    <#elseif specificity == "month">
        <#assign specLevel = 2 />
    <#-- allow to specify year, month and day using "date" -->
    <#elseif specificity == "day" || specificity == "date">
        <#assign specLevel = 3 />
    <#elseif specificity == "hour">
        <#assign specLevel = 4 />
    <#-- allow to specify hours and minutes using "time" -->
    <#elseif specificity == "minute" || specificity == "time">
        <#assign specLevel = 5 />
    <#elseif specificity == "second">
        <#assign specLevel = 6 />
    </#if>
    <#if required == "year">
        <#assign reqLevel = 1 />
    <#elseif required == "month">
        <#assign reqLevel = 2 />
    <#-- allow to require year, month and day using "date" -->
    <#elseif required == "day" || required == "date">
        <#assign reqLevel = 3 />
    <#elseif required == "hour">
        <#assign reqLevel = 4 />
    <#-- allow to require hours and minutes using "time" -->
    <#elseif required == "minute" || required == "time">
        <#assign reqLevel = 5 />
    <#elseif required == "second">
        <#assign reqLevel = 6 />
    <#elseif required == "none">
        <#assign reqLevel = 0 />
    </#if>

    <fieldset id="dateTime">
        <#if specLevel gte 1>
            <#-- Only text input field in the mix. We should have some validation to ensure it's a valid year (4 digits, integer, etc) -->
            <label for="${fieldName}.year">Year<#if reqLevel gte 1> <span class="requiredHint">*</span></#if></label>
            <input class="text-field" name="${fieldName}.year" id="${fieldName}.year" type="text" value="${year!}" size="4" maxlength="4"<#if reqLevel gte 1>required </#if>/>
        </#if>

        <#if specLevel gte 2>
            <label for="${fieldName}.month">Month<#if reqLevel gte 2> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.month" id="${fieldName}.month" <#if reqLevel gte 2>required </#if>>
                <option value=""<#if !month??> selected="selected"</#if>>month</option>
                <#assign numMonths = 12 />
                <#list 1..numMonths as currentMonth>
                    <option value="${currentMonth?string('00')}"<#if month == "${currentMonth}"> selected="selected"</#if>>${currentMonth}</option>
                </#list>
            </select>
        </#if>

        <#if specLevel gte 3>
            <label for="${fieldName}.day">Day<#if reqLevel gte 3> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.day" id="${fieldName}.day" <#if reqLevel gte 3>required </#if>>
                <option value=""<#if !day??> selected="selected"</#if>>day</option>
                <#assign numDays = 31 />
                <#list 1..numDays as currentDay>
                    <option value="${currentDay?string('00')}"<#if day == "${currentDay}"> selected="selected"</#if>>${currentDay}</option>
                </#list>
            </select>
        </#if>

        <#if specLevel gte 4>
            <#-- We'll need to make this more flexible to support 24 hour display down the road. For now assuming 12h with am/pm -->
            <label for="${fieldName}.hour">Hour<#if reqLevel gte 4> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.hour" id="${fieldName}.hour" <#if reqLevel gte 3>required </#if>>
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

        <#if specLevel gte 5>
            <label for="${fieldName}.minute">Minutes<#if reqLevel gte 5> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.minute" id="${fieldName}.minute" <#if reqLevel gte 5>required </#if>>
                <option value=""<#if !minute??> selected="selected"</#if>>minutes</option>
                <#assign numMinutes = 59 />
                <#list 1..numMinutes as currentMinute>
                    <option value="${currentMinute?string('00')}"<#if minute == "${currentMinute}"> selected="selected"</#if>>${currentMinute}</option>
                </#list>
            </select>
        </#if>

        <#if specLevel gte 6>
            <label for="${fieldName}.second">Seconds<#if reqLevel gte 6> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.second" id="${fieldName}.second" <#if reqLevel gte 6>required </#if>>
                <option value=""<#if !second??> selected="selected"</#if>>seconds</option>
                <#assign numMinutes = 59 />
                <#list 1..numMinutes as currentSecond>
                    <option value="${currentSecond?string('00')}"<#if second == "${currentSecond}"> selected="selected"</#if>>${currentSecond}</option>
                </#list>
            </select>
        </#if>
    </fieldset>
</#macro>