<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->


<#if vocabularyService?has_content>
	<h2 class="mainPropGroup">Vocabulary Service</h2>
	<ul>
		<li>
			<#switch vocabularyService[0]['vocabService']>
				<#case "AGROVOC"> 
					AGROVOC (Agricultural Vocabulary)
					<#break>
				<#case "GEMET">
					GEMET  (GEneral Multilingual Environmental Thesaurus) 
					<#break>
				<#case "LCSH">
					LCSH  (Library of Congress Subject Headings) 
					<#break>
				<#case "UMLS">
					UMLS  (Unified Medical Language System) 
					<#break>
				<#default>
					${vocabularyService[0]['vocabService']!}
			</#switch> 
		</li>
	</ul>
</#if>
