<#assign fsmIcon      = "${urls.images}/individual/fsm.png">
<#assign wordleIcon   = "${urls.images}/individual/wordle.png">
<#assign pieIcon      = "${urls.images}/individual/pie_chart.png">
<#assign coAuthorIcon = "${urls.images}/visualization/coauthorship/co_author_icon.png">
<#assign fsmUrl       = individual.getFSMFacultyProfileUrl()>
<#assign uriSuffix    = individual.getUriSuffix()>

<#-- Determine whether this person is an author -->
<#assign isAuthor = p.hasVisualizationStatements(propertyGroups, "${core}relatedBy", "${core}Authorship") />

<#-- Determine whether this person is involved in any grants -->
<#assign obo_RO53 = "http://purl.obolibrary.org/obo/RO_0000053">

<#assign isInvestigator = (p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}InvestigatorRole") || p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}PrincipalInvestigatorRole") ||  p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}CoPrincipalInvestigatorRole"))>

<#if (!isAuthor && !isInvestigator)>
    ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/visualization/visualization.css" />')}
</#if>

<#if fsmUrl?has_content>
    <div class="collaboratorship-link-separator"></div>
        
    <div id="coauthorship_link_container" class="collaboratorship-link-container">
        <div class="collaboratorship-icon">
            <a href="${fsmUrl}" title="FSM Faculty Profile" target="_blank">
                <img src="${fsmIcon}" alt="FSM Faculty Profile" width="25px" height="25px" />
            </a>
        </div>
        <div class="collaboratorship-link">
            <a href="${fsmUrl}" title="FSM Faculty Profile" target="_blank">
                FSM Faculty Profile
            </a>
        </div>
    </div>
</#if>

<#if uriSuffix?has_content>
    <div class="collaboratorship-link-separator"></div>
    <div id="coauthorship_link_container" class="collaboratorship-link-container">
        <div class="collaboratorship-icon">
            <a href="http://vtfsmvivo.fsm.northwestern.edu/vivo-vl/d3/${uriSuffix}/investigator_chord" title="Chord Diagram" target="_blank">
                <img src="${coAuthorIcon}" alt="Chord Diagram" width="25px" height="25px" />
            </a>
        </div>
        <div class="collaboratorship-link">
            <a href="http://vtfsmvivo.fsm.northwestern.edu/vivo-vl/d3/${uriSuffix}/investigator_chord" title="Chord Diagram" target="_blank">
                Chord Diagram
            </a>
        </div>
    </div>
    <div class="collaboratorship-link-separator"></div>
    <div id="coauthorship_link_container" class="collaboratorship-link-container">
        <div class="collaboratorship-icon">
            <a href="http://vtfsmvivo.fsm.northwestern.edu/vivo-vl/d3/${uriSuffix}/wordle" title="Chord Diagram" target="_blank">
                <img src="${wordleIcon}" alt="Wordle" width="25px" height="25px" />
            </a>
        </div>
        <div class="collaboratorship-link">
            <a href="http://vtfsmvivo.fsm.northwestern.edu/vivo-vl/d3/${uriSuffix}/wordle" title="Chord Diagram" target="_blank">
                Wordle
            </a>
        </div>
    </div>
    <div class="collaboratorship-link-separator"></div>
    <div id="coauthorship_link_container" class="collaboratorship-link-container">
        <div class="collaboratorship-icon">
            <a href="http://vtfsmvivo.fsm.northwestern.edu/vivo-vl/highcharts/${uriSuffix}/publication_types_pie_chart" title="Publication Type Pie Chart" target="_blank">
                <img src="${pieIcon}" alt="Publication Type Pie Chart" width="25px" height="25px" />
            </a>
        </div>
        <div class="collaboratorship-link">
            <a href="http://vtfsmvivo.fsm.northwestern.edu/vivo-vl/highcharts/${uriSuffix}/publication_types_pie_chart" title="Publication Type Pie Chart" target="_blank">
                Publication Types
            </a>
        </div>
    </div>
</#if>