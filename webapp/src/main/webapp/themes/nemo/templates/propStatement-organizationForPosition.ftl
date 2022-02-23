<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Custom object property statement view for faux property "people". See the PropertyConfig.n3 file for details.
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->

<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>
<@showPosition statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showPosition statement>
	<#if statement.hideThis?has_content>
		<span class="hideThis">&nbsp;</span>
		<script type="text/javascript" >
			$('span.hideThis').parent().parent().addClass("hideThis");
			if ( $('h3#relatedBy-Position').attr('class').length == 0 ) {
				$('h3#relatedBy-Position').addClass('hiddenPeople');
			}
			$('span.hideThis').parent().remove();
		</script>
	<#else>    
		<div class="panel panel-default">
			<div class="panel-body">
				<div class="row">
					<div class="col-md-4">
						<#if statement.downloadLocation??>
							<div id="photo-wrapper">
								<img width="190" title="image" class="img-rounded" alt="image" src="${statement.downloadLocation?replace("http:","https:")}">
							</div>
						<#else>
							<div id="photo-wrapper">
								<img width="190" title="no image" class="img-rounded" alt="placeholder image" src="/images/placeholders/person.thumbnail.jpg">
							</div>
						</#if>		
					</div>
					<div class="col-md-8">
						<h4>
							<#local linkedIndividual>
								<#if statement.person??>
									<a href="${profileUrl(statement.uri("person"))}" title="${i18n().person_name}" style="font-weight: bold;">${statement.personName}</a>
								<#else>
									<#-- This shouldn't happen, but we must provide for it -->
									<a href="${profileUrl(statement.uri("position"))}" title="${i18n().missing_person_in_posn}">${i18n().missing_person_in_posn}</a>
								</#if>
							</#local>

							<@s.join [ linkedIndividual, statement.positionTitle! ] /> 
							
							<@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />		
						</h4>
						<#if statement.overview??>
							<p>
								<#if (statement.overview?length > 500)>
									${statement.overview?substring(0, 500)}...
								<#else>
									${statement.overview}
								</#if>
							</p>
						</#if>
					</div>
				</div>
			</div>
		</div>
	</#if>
</#macro>