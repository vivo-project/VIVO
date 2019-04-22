<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for sparkline visualization on individual profile page -->

<#-- Determine whether this person is an author -->
<#assign isAuthor = p.hasVisualizationStatements(propertyGroups, "${core}relatedBy", "${core}Authorship") />

<#-- Determine whether this person is involved in any grants -->
<#assign obo_RO53 = "http://purl.obolibrary.org/obo/RO_0000053">

<#assign isInvestigator = ( p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}InvestigatorRole") ||
                            p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}PrincipalInvestigatorRole") ||
                            p.hasVisualizationStatements(propertyGroups, "${obo_RO53}", "${core}CoPrincipalInvestigatorRole") ) >

<#if (isAuthor || isInvestigator)>

    ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/visualization/visualization.css" />')}
    <#assign standardVisualizationURLRoot ="/visualization">

        <#if isAuthor>
            ${scripts.add('<script type="text/javascript" src="${urls.base}/js/d3.min.js"></script>')}

            <#assign coAuthorIcon = "${urls.images}/visualization/coauthorship/co_author_icon.png">
            <#assign mapOfScienceIcon = "${urls.images}/visualization/mapofscience/scimap_icon.png">
            <#assign coAuthorVisUrl = individual.coAuthorVisUrl()>
            <#assign mapOfScienceVisUrl = individual.mapOfScienceUrl()>

            <span id="publicationsHeading">${i18n().publications_in_vivo}</span>

            <svg width="100%" id="publicationsChart" onload="renderPublicationsChart()" onresize="renderPublicationsChart()">
            </svg>

            <script>
                var dataUrl = '${urls.base}/visualizationAjax?vis=cumulative_pub_count&uri=${individual.uri?url}';

                function renderPublicationsChart() {
                    var chartWidth = parseInt(d3.select("#publicationsChart").style("width"),10);
//                    var chartHeight =  parseInt(d3.select("#publicationsChart").style("height"),10);
                    var chartHeight = chartWidth * (2/3);

                    d3.select("#publicationsChart").selectAll("*").remove();
                    d3.select("#publicationsChart").style("height", chartHeight);

                    var svg = d3.select("#publicationsChart"),
                            margin = {top: 30, right: 20, bottom: 30, left: 40},
                            width = (chartWidth - margin.left - margin.right),
                            height = (chartHeight - margin.top - margin.bottom),
                            g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    svg.attr("width", chartWidth).attr("height",chartHeight)

                    var x = d3.scaleBand()
                            .rangeRound([0, width])
                            .paddingInner(0.05)
                            .align(0.1);

                    var y = d3.scaleLinear()
                            .rangeRound([height, 0]);

                    var z = d3.scaleOrdinal()
                            .range(["#777777", "#1f77b4", "#aec7e8", "#ff7f0e"]);

                    d3.csv(dataUrl, function (d, i, columns) {
                        for (i = 1, t = 0; i < columns.length; ++i) t += d[columns[i]] = +d[columns[i]];
                        d.total = t;
                        return d;
                    }, function (error, data) {
                        if (error) throw error;

                        var keys = data.columns.slice(1);

                        x.domain(data.map(function (d) {
                            return d.Year;
                        }));
                        y.domain([0, d3.max(data, function (d) {
                            return d.total;
                        })]).nice();
                        z.domain(keys);

                        g.append("g")
                                .selectAll("g")
                                .data(d3.stack().keys(keys)(data))
                                .enter().append("g")
                                .attr("fill", function (d) {
                                    return z(d.key);
                                })
                                .selectAll("rect")
                                .data(function (d) {
                                    return d;
                                })
                                .enter().append("rect")
                                .attr("x", function (d) {
                                    return x(d.data.Year);
                                })
                                .attr("y", function (d) {
                                    return y(d[1]);
                                })
                                .attr("height", function (d) {
                                    return y(d[0]) - y(d[1]);
                                })
                                .attr("width", x.bandwidth());

                        g.append("g")
                                .attr("class", "axis")
                                .attr("transform", "translate(0," + height + ")")
                                .call(d3.axisBottom(x));

                        g.append("g")
                                .attr("class", "axis")
                                .call(d3.axisLeft(y).ticks(null, "s"))
                                .append("text")
                                .attr("x", 2)
                                .attr("y", y(y.ticks().pop()) + 0.5)
                                .attr("dy", "0.32em")
                                .attr("fill", "#000");

                        var legend = g.append("g")
                                .attr("font-family", "sans-serif")
                                .attr("font-size", 10)
                                .attr("text-anchor", "end")
                                .selectAll("g")
                                .data(keys.slice(1,4).reverse())
                                .enter().append("g")
                                .attr("transform", function (d, i) {
                                    return "translate(-" + (200 - i * 80) +",-25)";
                                });

                        legend.append("rect")
                                .attr("x", width - 19)
                                .attr("width", 19)
                                .attr("height", 19)
                                .attr("fill", z);

                        legend.append("text")
                                .attr("x", width - 24)
                                .attr("y", 9.5)
                                .attr("dy", "0.32em")
                                .text(function (d) {
                                    return d;
                                });
                    });
                }
            </script>

            <div class="visualization-buttons">
                <div id="coauthorship_link_container" class="collaboratorship-link-container">
                    <a href="${coAuthorVisUrl}" title="${i18n().co_author_network}" class="btn btn-info" role="button">
                        <img src="${coAuthorIcon}" alt="${i18n().co_author}" width="25px" height="25px" />
                        ${i18n().co_author_network}
                    </a>
                </div>

                <div id="mapofscience_link_container" class="collaboratorship-link-container">
                    <a href="${mapOfScienceVisUrl}" title="${i18n().map_of_science}" class="btn btn-info" role="button">
                        <img src="${mapOfScienceIcon}" alt="${i18n().map_of_science}" width="25px" height="25px" />
                        ${i18n().map_of_science_capitalized}
                    </a>
                </div>

                <#if isInvestigator>
                    <#assign coInvestigatorVisUrl = individual.coInvestigatorVisUrl()>
                    <#assign coInvestigatorIcon = "${urls.images}/visualization/coauthorship/co_investigator_icon.png">

                    <div id="coinvestigator_link_container" class="collaboratorship-link-container">
                        <a href="${coInvestigatorVisUrl}" title="${i18n().co_investigator_network}" class="btn btn-info" role="button">
                            <img src="${coInvestigatorIcon}" alt="${i18n().co_investigator_network}" width="25px" height="25px" />
                        ${i18n().co_investigator_network_capitalized}
                        </a>
                    </div>
                </#if>
            </div>
        </#if>
</#if>
