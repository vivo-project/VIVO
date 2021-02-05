<#-- $This file is distributed under the terms of the license in LICENSE$ -->

<#assign standardVisualizationURLRoot ="/visualization">
<#assign shortVisualizationURLRoot ="/vis">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">

<#assign egoURI ="${egoURIParam?url}">
<#assign egoCoAuthorshipDataFeederURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthor_network_stream&labelField=label'>

<#if egoLocalName?has_content >
    <#assign coprincipalinvestigatorURL = '${urls.base}${shortVisualizationURLRoot}/investigator-network/${egoLocalName}'>
<#else>
    <#assign coprincipalinvestigatorURL = '${urls.base}${shortVisualizationURLRoot}/investigator-network/?uri=${egoURI}'>
</#if>

<#assign egoCoAuthorsListDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthors'>
<#assign egoCoAuthorshipNetworkDataFileURL = '${urls.base}${dataVisualizationURLRoot}?vis=coauthorship&uri=${egoURI}&vis_mode=coauthor_network_download'>

<#assign googleVisualizationAPI = 'https://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D'>
<#assign coAuthorPersonLevelJavaScript = '${urls.base}/js/visualization/coauthorship/coauthorship-personlevel.js'>
<#assign commonPersonLevelJavaScript = '${urls.base}/js/visualization/personlevel/person-level.js'>

<#assign coInvestigatorIcon = '${urls.images}/visualization/coauthorship/co_investigator_icon.png'>

<script type="text/javascript" src="${googleVisualizationAPI}"></script>

<script language="JavaScript" type="text/javascript">
<!--
// -----------------------------------------------------------------------------
// Globals
var egoURI = "${egoURI}";
var unEncodedEgoURI = "${egoURIParam}";
var egoCoAuthorshipDataFeederURL = "${egoCoAuthorshipDataFeederURL}";
var egoCoAuthorsListDataFileURL = "${egoCoAuthorsListDataFileURL}";
var contextPath = "${urls.base}";

var visualizationDataRoot = "${dataVisualizationURLRoot}";
// -->

var i18nStringsCoauthorship = {
    coAuthorsString: "${i18n().co_authors_capitalized?js_string}",
    authorString: "${i18n().author_capitalized?js_string}",
    publicationsWith: "${i18n().publications_with?js_string}",
    publicationsString: "${i18n().through_today?js_string}",
    coauthorsString: "${i18n().co_author_s_capitalized?js_string}"
};
var i18nStringsPersonLvl = {
    fileCapitalized: "${i18n().file_capitalized?js_string}",
    contentRequiresFlash: "${i18n().content_requires_flash?js_string}",
    getFlashString: "${i18n().get_flash?js_string}"
};
</script>

<script type="text/javascript" src="${coAuthorPersonLevelJavaScript}"></script>
<script type="text/javascript" src="${commonPersonLevelJavaScript}"></script>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/d3.min.js"></script>')}

${stylesheets.add('<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/personlevel/page.css" />',
                  '<link rel="stylesheet" type="text/css" href="${urls.base}/css/visualization/visualization.css" />')}

<#assign egoVivoProfileURL = "${urls.base}/individual?uri=${egoURI}" />

<script language="JavaScript" type="text/javascript">

$(document).ready(function(){

    processProfileInformation("ego_label",
                              "ego_moniker",
                              "ego_profile_image",
                              jQuery.parseJSON(getWellFormedURLs("${egoURIParam}", "profile_info")));

    <#if (numOfCoAuthorShips?? && numOfCoAuthorShips <= 0) || (numOfAuthors?? && numOfAuthors <= 0) >
        if ($('#ego_label').text().length > 0) {
            setProfileName('no_coauthorships_person', $('#ego_label').text());
        }
    </#if>


    $.ajax({
            url: "${urls.base}/visualizationAjax",
            data: ({vis: "utilities", vis_mode: "SHOW_GRANTS_LINK", uri: '${egoURIParam}'}),
            dataType: "json",
            success:function(data){

                /*
                Collaboratorship links do not show up by default. They should show up only if there any data to
                show on that page.
                */
                if (data.numOfGrants !== undefined && data.numOfGrants > 0) {
                       $(".toggle_visualization").show();
                }

            }
        });

// RENDER CHORD

    var labels = [];
    var uris   = [];
    var matrix = [];

    var matrixX = 0;
    <#list coAuthorshipData.collaborationMatrix as row>
        matrix[matrixX] = [];
        <#list row as cell>
            matrix[matrixX].push(${cell?c});
        </#list>
        matrixX++;
    </#list>
    <#list coAuthorshipData.collaborators as collaborator>
        labels.push("${collaborator.collaboratorName}");
        uris.push("${collaborator.collaboratorURI}");
    </#list>

    var chord = d3.chord()
            .padAngle(0.05)
            .sortSubgroups(d3.descending);

    var width  = 725;
    var height = 725;
    var padding = 175;
    var inner_radius = Math.min(width, height) * 0.37;
    var outer_radius = Math.min(width, height) * 0.39;

    var fill = d3.scaleOrdinal()
            .domain(d3.range(20))
            .range(["#000000", "#1f77b4", "#aec7e8", "#ff7f0e", "#ffbb78",
                "#2ca02c", "#98df8a", "#d62728", "#ff9896", "#9467bd",
                "#c5b0d5", "#8c564b", "#c49c94", "#e377c2", "#f7b6d2",
                "#7f7f7f", "#c7c7c7", "#bcbd22", "#dbdb8d", "#17becf"
    ]);
    // #9edae5

    var svg = d3.select('#chord').append('svg')
            .attr('width', width+padding)
            .attr('height', height+padding)
            .append('g').attr('transform', 'translate(' + (width+padding) / 2 + ',' + (height+padding) / 2 +')')
            .datum(chord(matrix));

    svg.append('g').selectAll('path').data(function(chords) { return chords.groups; }).enter()
            .append('path').style('fill', function(val) { return fill(val.index); })
            .style('stroke', function(val) { return fill(val.index); })
            .attr('d', d3.arc().innerRadius(inner_radius).outerRadius(outer_radius))
            .on('click', chord_click())
            .on("mouseover", chord_hover(.05))
            .on("mouseout", chord_hover(.8));

    var group_ticks = function (d) {
        var k = (d.endAngle - d.startAngle) / d.value;
        return d3.range(d.value / 2, d.value, d.value / 2).map(function (v) {
            return {
                angle: v * k + d.startAngle,
                label: Math.round(d.value)
            };
        });
    };

    var chord_ticks = svg.append('g')
            .selectAll('g')
            .data(function (chords) { return chords.groups; })
            .enter().append('g')
            .selectAll('g')
            .data(group_ticks)
            .enter().append('g')
            .attr('transform', function (d) {
                return 'rotate(' + (d.angle * 180 / Math.PI - 90) + ') translate(' + outer_radius + ',0)';
            });

    svg.append('g')
            .attr('class', 'chord')
            .selectAll('path')
            .data(function (chords) { return chords; })
            .enter().append('path')
            .style('fill', function (d) { return fill(d.target.index); })
            .attr('d', d3.ribbon().radius(inner_radius))
            .style('opacity', .8);

    svg.append("g").selectAll(".arc")
            .data(function (chords) { return chords.groups; })
            .enter().append("svg:text")
            .attr("dy", ".35em")
            .attr("style", function(d) { return d.index == 0 ? "font-size: .75em; font-weight: bold;" : "font-size: .70em;"; } )
            .attr("text-anchor", function(d) { return ((d.startAngle + d.endAngle) / 2) > Math.PI ? "end" : null; })
            .attr("transform", function(d) {
                return "rotate(" + (((d.startAngle + d.endAngle) / 2) * 180 / Math.PI - 90) + ")"
                        + "translate(" + (height *.40) + ")"
                        + (((d.startAngle + d.endAngle) / 2) > Math.PI ? "rotate(180)" : "");
            })
            .text(function(d) {
                return labels[d.index];
            })
            .on('click', chord_click())
            .on("mouseover", chord_hover(.05))
            .on("mouseout", chord_hover(.8));

    function chord_hover(opacity) {
        return function(g, i) {
            if (opacity > .5) {
                var chordInfoDiv = d3.select('#chord-info-div');
                chordInfoDiv.style('display', 'none');
            } else {
                var hoverEvent = d3.event;
                var topPos = hoverEvent.pageY - 60;
                var leftPos = hoverEvent.pageX + 10;

                var chord = d3.select('#chord').node();
                var chordInfoDiv = d3.select('#chord-info-div');
                var hoverMsg = labels[i] + "<br/>";
                if (i > 0) {
                    hoverMsg += matrix[i][0] + " Joint ${i18n().publication_s_capitalized}<br/>";
                } else {
                    hoverMsg += "${coAuthorshipData.collaboratorsCount} ${i18n().co_author_s_capitalized}<br/>";
                }

                chordInfoDiv.html(hoverMsg);
                chordInfoDiv.style('display', 'block');
                chordInfoDiv.style('position', 'absolute');

                if (d3.mouse(chord)[1] > height / 2) {
                    topPos += 80;
                }
                chordInfoDiv.style('top', topPos + 'px');

                if (hoverEvent.pageX > document.body.clientWidth / 2) {
                    leftPos = hoverEvent.pageX + 10;
                } else {
                    leftPos = hoverEvent.pageX - (10 + chordInfoDiv.node().getBoundingClientRect().width);
                }
                chordInfoDiv.style('left', leftPos + 'px');
            }

            svg.selectAll(".chord path")
                    .filter(function(d) { return d.source.index != i && d.target.index != i; })
                    .transition()
                    .style("opacity", opacity);
        }
    }

    function chord_click() {
        return function (g, i) {
            if (i > 0) {
                window.location.href = getWellFormedURLs(uris[i], "profile");
            }
        };
    }
});
</script>

<div id="body">
	<div  class="sub_headings"><h2><a href="${egoVivoProfileURL}" title="${i18n().author_name}"><span id="ego_label"></span></a><br />${i18n().co_author_network} </h2></div>
    <#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) || (numOfAuthors?? && numOfAuthors > 0) >
        <div class = "graphml-file-link">(<a href="${egoCoAuthorshipNetworkDataFileURL}" title="GraphML ${i18n().file}">GraphML ${i18n().file}</a>)</div>
    </#if>

    <div class = "toggle_visualization">
        <div id="coinvestigator_link_container" class="collaboratorship-link-container">
            <div class="collaboratorship-icon"><a href="${coprincipalinvestigatorURL}" title="${i18n().co_investigator}"><img src="${coInvestigatorIcon}" alt="${i18n().co_investigator_icon}"/></a></div>
            <div class="collaboratorship-link">
                <h3><a href="${coprincipalinvestigatorURL}" title="${i18n().co_investigator_network}">${i18n().co_investigator_network_capitalized}</a></h3>
            </div>
        </div>
    </div>

    <#if (builtFromCacheTime??) >
        <div class="cache-info-small">${i18n().using_cache_time} ${builtFromCacheTime?time} (${builtFromCacheTime?date?string("MMM dd yyyy")})</div>
    </#if>
    <div style="clear:both;"></div>

    <#if (numOfAuthors?? && numOfAuthors > 0) >
    <#else>

        <span id="no_coauthorships">${i18n().no_papers_for}
            <a href="${egoVivoProfileURL}" title="${i18n().co_authorship}"><span id="no_coauthorships_person" class="author_name">${i18n().this_author}</span></a> ${i18n().in_the_vivo_db}
        </span>

    </#if>

    <#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) || (numOfAuthors?? && numOfAuthors > 0) >

        <div id="bodyPannel">
            <div id="chord" style="float: right;"></div>
        </div>
    </#if>

    <div style="clear:both"></div>
        <div id="incomplete-data-small">${i18n().incomplete_data_note1}<p></p><p></p>
            <#if user.loggedIn >
                ${i18n().incomplete_data_note2}
            <#else>
                ${i18n().incomplete_data_note3}
            </#if>
        </div>
        <p></p>
    <div style="clear:both"></div>

    <#if (numOfAuthors?? && numOfAuthors > 0) >

        <#-- Sparkline -->
        <div id="sparkline-container-full">

            <#assign displayTable = false />

            <#assign sparklineVO = egoPubSparklineVO />
            <div id="publication-count-sparkline-include"><#include "personPublicationSparklineContent.ftl"></div>

            <#assign sparklineVO = uniqueCoauthorsSparklineVO />
            <div id="coauthor-count-sparkline-include"><#include "coAuthorshipSparklineContent.ftl"></div>
        </div>

        <div class="vis_stats_full">

        <div class="sub_headings" id="table_heading"><h3>${i18n().tables_capitalized}</h3></div>

            <div class="vis-tables">

                <p id="publications_table_container" class="datatable">

                <#assign tableID = "publication_data_table" />
                <#assign tableCaption = "${i18n().publications_per_year} " />
                <#assign tableActivityColumnName = "${i18n().publications_capitalized}" />
                <#assign tableContent = egoPubSparklineVO.yearToActivityCount />
                <#assign fileDownloadLink = egoPubSparklineVO.downloadDataLink />

                <#include "yearToActivityCountTable.ftl">

                </p>

            </div>

            <#if (numOfCoAuthorShips?? && numOfCoAuthorShips > 0) >

                <div class="vis-tables">
                    <p id="coauth_table_container" class="datatable">
                        <#assign tableID = "coauthorships_table" />
                        <#assign tableCaption = "${i18n().co_authors_capitalized} " />
                        <#assign tableCollaboratorColumnName = "${i18n().author_capitalized}" />
                        <#assign tableActivityColumnName = "${i18n().publications_with}" />
                        <#assign tableContent = coAuthorshipData />
                        <#assign fileDownloadLink = uniqueCoauthorsSparklineVO.downloadDataLink />

                        <#include "collaboratorToActivityCountTable.ftl">
                    </p>
                </div>

            </#if>

            <div style="clear:both"></div>

        </div>

    </#if>

</div>
<div id="chord-info-div" style="display: none;"></div>
