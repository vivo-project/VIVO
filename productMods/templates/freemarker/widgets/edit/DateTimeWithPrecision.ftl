<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
This is the placeholder template for the dateTime with precision input element.
The UI team should replace any or all of the text in this file.
-->

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
            <input class="text-field" name="${fieldName}.year" id="${fieldName}.year" type="text" value="${year!}" size="4" <#if reqLevel gte 1>required </#if>/>
        </#if>

        <#if specLevel gte 2>
            <label for="${fieldName}.month">Month<#if reqLevel gte 2> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.month" id="${fieldName}.month" <#if reqLevel gte 2>required </#if>>
                <option value=""<#if month??> selected="selected"</#if>>month</option>
                <option value="01"<#if month == "01"> selected="selected"</#if>>1</option>
                <option value="02"<#if month == "02"> selected="selected"</#if>>2</option>
                <option value="03"<#if month == "03"> selected="selected"</#if>>3</option>
                <option value="04"<#if month == "04"> selected="selected"</#if>>4</option>
                <option value="05"<#if month == "05"> selected="selected"</#if>>5</option>
                <option value="06"<#if month == "06"> selected="selected"</#if>>6</option>
                <option value="07"<#if month == "07"> selected="selected"</#if>>7</option>
                <option value="08"<#if month == "08"> selected="selected"</#if>>8</option>
                <option value="09"<#if month == "09"> selected="selected"</#if>>9</option>
                <option value="10"<#if month == "10"> selected="selected"</#if>>10</option>
                <option value="11"<#if month == "11"> selected="selected"</#if>>11</option>
                <option value="12"<#if month == "12"> selected="selected"</#if>>12</option>
            </select>
        </#if>

        <#if specLevel gte 3>
            <label for="${fieldName}.day">Day<#if reqLevel gte 3> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.day" id="${fieldName}.day" <#if reqLevel gte 3>required </#if>>
                <option value=""<#if day??> selected="selected"</#if>>day</option>
                <option value="01"<#if day == "01"> selected="selected"</#if>>1</option>
                <option value="02"<#if day == "02"> selected="selected"</#if>>2</option>
                <option value="03"<#if day == "03"> selected="selected"</#if>>3</option>
                <option value="04"<#if day == "04"> selected="selected"</#if>>4</option>
                <option value="05"<#if day == "05"> selected="selected"</#if>>5</option>
                <option value="06"<#if day == "06"> selected="selected"</#if>>6</option>
                <option value="07"<#if day == "07"> selected="selected"</#if>>7</option>
                <option value="08"<#if day == "08"> selected="selected"</#if>>8</option>
                <option value="09"<#if day == "09"> selected="selected"</#if>>9</option>
                <option value="10"<#if day == "10"> selected="selected"</#if>>10</option>
                <option value="11"<#if day == "11"> selected="selected"</#if>>11</option>
                <option value="12"<#if day == "12"> selected="selected"</#if>>12</option>
                <option value="13"<#if day == "13"> selected="selected"</#if>>13</option>
                <option value="14"<#if day == "14"> selected="selected"</#if>>14</option>
                <option value="15"<#if day == "15"> selected="selected"</#if>>15</option>
                <option value="16"<#if day == "16"> selected="selected"</#if>>16</option>
                <option value="17"<#if day == "17"> selected="selected"</#if>>17</option>
                <option value="18"<#if day == "18"> selected="selected"</#if>>18</option>
                <option value="19"<#if day == "19"> selected="selected"</#if>>19</option>
                <option value="20"<#if day == "20"> selected="selected"</#if>>20</option>
                <option value="21"<#if day == "21"> selected="selected"</#if>>21</option>
                <option value="22"<#if day == "22"> selected="selected"</#if>>22</option>
                <option value="23"<#if day == "23"> selected="selected"</#if>>23</option>
                <option value="24"<#if day == "24"> selected="selected"</#if>>24</option>
                <option value="25"<#if day == "25"> selected="selected"</#if>>25</option>
                <option value="26"<#if day == "26"> selected="selected"</#if>>26</option>
                <option value="27"<#if day == "27"> selected="selected"</#if>>27</option>
                <option value="28"<#if day == "28"> selected="selected"</#if>>28</option>
                <option value="29"<#if day == "29"> selected="selected"</#if>>29</option>
                <option value="30"<#if day == "30"> selected="selected"</#if>>30</option>
                <option value="31"<#if day == "31"> selected="selected"</#if>>31</option>
            </select>
        </#if>

        <#if specLevel gte 4>
            <#-- We'll need to make this more flexible to support 24 hour display down the road. For now assuming 12h with am/pm -->
            <label for="${fieldName}.hour">Hour<#if reqLevel gte 4> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.hour" id="${fieldName}.hour" <#if reqLevel gte 3>required </#if>>
                <option value=""<#if hour??> selected="selected"</#if>>hour</option>
                <option value="00"<#if hour == "00"> selected="selected"</#if>>12am</option>
                <option value="01"<#if hour == "01"> selected="selected"</#if>>1am</option>
                <option value="02"<#if hour == "02"> selected="selected"</#if>>2am</option>
                <option value="03"<#if hour == "03"> selected="selected"</#if>>3am</option>
                <option value="04"<#if hour == "04"> selected="selected"</#if>>4am</option>
                <option value="05"<#if hour == "05"> selected="selected"</#if>>5am</option>
                <option value="06"<#if hour == "06"> selected="selected"</#if>>6am</option>
                <option value="07"<#if hour == "07"> selected="selected"</#if>>7am</option>
                <option value="08"<#if hour == "08"> selected="selected"</#if>>8am</option>
                <option value="09"<#if hour == "09"> selected="selected"</#if>>9am</option>
                <option value="10"<#if hour == "10"> selected="selected"</#if>>10am</option>
                <option value="11"<#if hour == "11"> selected="selected"</#if>>11am</option>
                <option value="12"<#if hour == "12"> selected="selected"</#if>>12pm</option>
                <option value="13"<#if hour == "13"> selected="selected"</#if>>1pm</option>
                <option value="14"<#if hour == "14"> selected="selected"</#if>>2pm</option>
                <option value="15"<#if hour == "15"> selected="selected"</#if>>3pm</option>
                <option value="16"<#if hour == "16"> selected="selected"</#if>>4pm</option>
                <option value="17"<#if hour == "17"> selected="selected"</#if>>5pm</option>
                <option value="18"<#if hour == "18"> selected="selected"</#if>>6pm</option>
                <option value="19"<#if hour == "19"> selected="selected"</#if>>7pm</option>
                <option value="20"<#if hour == "20"> selected="selected"</#if>>8pm</option>
                <option value="21"<#if hour == "21"> selected="selected"</#if>>9pm</option>
                <option value="22"<#if hour == "22"> selected="selected"</#if>>10pm</option>
                <option value="23"<#if hour == "23"> selected="selected"</#if>>11pm</option>
            </select>
        </#if>

        <#if specLevel gte 5>
            <label for="${fieldName}.minute">Minutes<#if reqLevel gte 5> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.minute" id="${fieldName}.minute" <#if reqLevel gte 5>required </#if>>
                <option value=""<#if minute??> selected="selected"</#if>>minutes</option>
                <option value="01"<#if minute == "01"> selected="selected"</#if>>1</option>
                <option value="02"<#if minute == "02"> selected="selected"</#if>>2</option>
                <option value="03"<#if minute == "03"> selected="selected"</#if>>3</option>
                <option value="04"<#if minute == "04"> selected="selected"</#if>>4</option>
                <option value="05"<#if minute == "05"> selected="selected"</#if>>5</option>
                <option value="06"<#if minute == "06"> selected="selected"</#if>>6</option>
                <option value="07"<#if minute == "07"> selected="selected"</#if>>7</option>
                <option value="08"<#if minute == "08"> selected="selected"</#if>>8</option>
                <option value="09"<#if minute == "09"> selected="selected"</#if>>9</option>
                <option value="10"<#if minute == "10"> selected="selected"</#if>>10</option>
                <option value="11"<#if minute == "11"> selected="selected"</#if>>11</option>
                <option value="12"<#if minute == "12"> selected="selected"</#if>>12</option>
                <option value="13"<#if minute == "13"> selected="selected"</#if>>13</option>
                <option value="14"<#if minute == "14"> selected="selected"</#if>>14</option>
                <option value="15"<#if minute == "15"> selected="selected"</#if>>15</option>
                <option value="16"<#if minute == "16"> selected="selected"</#if>>16</option>
                <option value="17"<#if minute == "17"> selected="selected"</#if>>17</option>
                <option value="18"<#if minute == "18"> selected="selected"</#if>>18</option>
                <option value="19"<#if minute == "19"> selected="selected"</#if>>19</option>
                <option value="20"<#if minute == "20"> selected="selected"</#if>>20</option>
                <option value="21"<#if minute == "21"> selected="selected"</#if>>21</option>
                <option value="22"<#if minute == "22"> selected="selected"</#if>>22</option>
                <option value="23"<#if minute == "23"> selected="selected"</#if>>23</option>
                <option value="24"<#if minute == "24"> selected="selected"</#if>>24</option>
                <option value="25"<#if minute == "25"> selected="selected"</#if>>25</option>
                <option value="26"<#if minute == "26"> selected="selected"</#if>>26</option>
                <option value="27"<#if minute == "27"> selected="selected"</#if>>27</option>
                <option value="28"<#if minute == "28"> selected="selected"</#if>>28</option>
                <option value="29"<#if minute == "29"> selected="selected"</#if>>29</option>
                <option value="30"<#if minute == "30"> selected="selected"</#if>>30</option>
                <option value="31"<#if minute == "31"> selected="selected"</#if>>31</option>
                <option value="32"<#if minute == "32"> selected="selected"</#if>>32</option>
                <option value="33"<#if minute == "33"> selected="selected"</#if>>33</option>
                <option value="34"<#if minute == "34"> selected="selected"</#if>>34</option>
                <option value="35"<#if minute == "35"> selected="selected"</#if>>35</option>
                <option value="36"<#if minute == "36"> selected="selected"</#if>>36</option>
                <option value="37"<#if minute == "37"> selected="selected"</#if>>37</option>
                <option value="38"<#if minute == "38"> selected="selected"</#if>>38</option>
                <option value="39"<#if minute == "39"> selected="selected"</#if>>39</option>
                <option value="40"<#if minute == "40"> selected="selected"</#if>>40</option>
                <option value="41"<#if minute == "41"> selected="selected"</#if>>41</option>
                <option value="42"<#if minute == "42"> selected="selected"</#if>>42</option>
                <option value="43"<#if minute == "43"> selected="selected"</#if>>43</option>
                <option value="44"<#if minute == "44"> selected="selected"</#if>>44</option>
                <option value="45"<#if minute == "45"> selected="selected"</#if>>45</option>
                <option value="46"<#if minute == "46"> selected="selected"</#if>>46</option>
                <option value="47"<#if minute == "47"> selected="selected"</#if>>47</option>
                <option value="48"<#if minute == "48"> selected="selected"</#if>>48</option>
                <option value="49"<#if minute == "49"> selected="selected"</#if>>49</option>
                <option value="50"<#if minute == "50"> selected="selected"</#if>>50</option>
                <option value="51"<#if minute == "51"> selected="selected"</#if>>51</option>
                <option value="52"<#if minute == "52"> selected="selected"</#if>>52</option>
                <option value="53"<#if minute == "53"> selected="selected"</#if>>53</option>
                <option value="54"<#if minute == "54"> selected="selected"</#if>>54</option>
                <option value="55"<#if minute == "55"> selected="selected"</#if>>55</option>
                <option value="56"<#if minute == "56"> selected="selected"</#if>>56</option>
                <option value="57"<#if minute == "57"> selected="selected"</#if>>57</option>
                <option value="58"<#if minute == "58"> selected="selected"</#if>>58</option>
                <option value="59"<#if minute == "59"> selected="selected"</#if>>59</option>
            </select>
        </#if>

        <#if specLevel gte 6>
            <label for="${fieldName}.second">Seconds<#if reqLevel gte 6> <span class="requiredHint">*</span></#if></label>
            <select name="${fieldName}.second" id="${fieldName}.second" <#if reqLevel gte 6>required </#if>>
                <option value=""<#if second??> selected="selected"</#if>>seconds</option>
                <option value="01"<#if second == "01"> selected="selected"</#if>>1</option>
                <option value="02"<#if second == "02"> selected="selected"</#if>>2</option>
                <option value="03"<#if second == "03"> selected="selected"</#if>>3</option>
                <option value="04"<#if second == "04"> selected="selected"</#if>>4</option>
                <option value="05"<#if second == "05"> selected="selected"</#if>>5</option>
                <option value="06"<#if second == "06"> selected="selected"</#if>>6</option>
                <option value="07"<#if second == "07"> selected="selected"</#if>>7</option>
                <option value="08"<#if second == "08"> selected="selected"</#if>>8</option>
                <option value="09"<#if second == "09"> selected="selected"</#if>>9</option>
                <option value="10"<#if second == "10"> selected="selected"</#if>>10</option>
                <option value="11"<#if second == "11"> selected="selected"</#if>>11</option>
                <option value="12"<#if second == "12"> selected="selected"</#if>>12</option>
                <option value="13"<#if second == "13"> selected="selected"</#if>>13</option>
                <option value="14"<#if second == "14"> selected="selected"</#if>>14</option>
                <option value="15"<#if second == "15"> selected="selected"</#if>>15</option>
                <option value="16"<#if second == "16"> selected="selected"</#if>>16</option>
                <option value="17"<#if second == "17"> selected="selected"</#if>>17</option>
                <option value="18"<#if second == "18"> selected="selected"</#if>>18</option>
                <option value="19"<#if second == "19"> selected="selected"</#if>>19</option>
                <option value="20"<#if second == "20"> selected="selected"</#if>>20</option>
                <option value="21"<#if second == "21"> selected="selected"</#if>>21</option>
                <option value="22"<#if second == "22"> selected="selected"</#if>>22</option>
                <option value="23"<#if second == "23"> selected="selected"</#if>>23</option>
                <option value="24"<#if second == "24"> selected="selected"</#if>>24</option>
                <option value="25"<#if second == "25"> selected="selected"</#if>>25</option>
                <option value="26"<#if second == "26"> selected="selected"</#if>>26</option>
                <option value="27"<#if second == "27"> selected="selected"</#if>>27</option>
                <option value="28"<#if second == "28"> selected="selected"</#if>>28</option>
                <option value="29"<#if second == "29"> selected="selected"</#if>>29</option>
                <option value="30"<#if second == "30"> selected="selected"</#if>>30</option>
                <option value="31"<#if second == "31"> selected="selected"</#if>>31</option>
                <option value="32"<#if second == "32"> selected="selected"</#if>>32</option>
                <option value="33"<#if second == "33"> selected="selected"</#if>>33</option>
                <option value="34"<#if second == "34"> selected="selected"</#if>>34</option>
                <option value="35"<#if second == "35"> selected="selected"</#if>>35</option>
                <option value="36"<#if second == "36"> selected="selected"</#if>>36</option>
                <option value="37"<#if second == "37"> selected="selected"</#if>>37</option>
                <option value="38"<#if second == "38"> selected="selected"</#if>>38</option>
                <option value="39"<#if second == "39"> selected="selected"</#if>>39</option>
                <option value="40"<#if second == "40"> selected="selected"</#if>>40</option>
                <option value="41"<#if second == "41"> selected="selected"</#if>>41</option>
                <option value="42"<#if second == "42"> selected="selected"</#if>>42</option>
                <option value="43"<#if second == "43"> selected="selected"</#if>>43</option>
                <option value="44"<#if second == "44"> selected="selected"</#if>>44</option>
                <option value="45"<#if second == "45"> selected="selected"</#if>>45</option>
                <option value="46"<#if second == "46"> selected="selected"</#if>>46</option>
                <option value="47"<#if second == "47"> selected="selected"</#if>>47</option>
                <option value="48"<#if second == "48"> selected="selected"</#if>>48</option>
                <option value="49"<#if second == "49"> selected="selected"</#if>>49</option>
                <option value="50"<#if second == "50"> selected="selected"</#if>>50</option>
                <option value="51"<#if second == "51"> selected="selected"</#if>>51</option>
                <option value="52"<#if second == "52"> selected="selected"</#if>>52</option>
                <option value="53"<#if second == "53"> selected="selected"</#if>>53</option>
                <option value="54"<#if second == "54"> selected="selected"</#if>>54</option>
                <option value="55"<#if second == "55"> selected="selected"</#if>>55</option>
                <option value="56"<#if second == "56"> selected="selected"</#if>>56</option>
                <option value="57"<#if second == "57"> selected="selected"</#if>>57</option>
                <option value="58"<#if second == "58"> selected="selected"</#if>>58</option>
                <option value="59"<#if second == "59"> selected="selected"</#if>>59</option>
            </select>
        </#if>
    </fieldset>
</#macro>