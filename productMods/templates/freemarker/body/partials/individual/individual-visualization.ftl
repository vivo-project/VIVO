<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Individual visualization template -->

 <#if individual.organization >
    <#-- Logically we only need section#temporal-graph, but css may depend on the outer sections. Leaving here for UI
    team to check. -->
    <section id="visualization" role="region">
        <section id="sparklines-publications" role="region">
            <section id="temporal-graph" role="region">
                <h3><img src="${urls.images}/visualization/temporal_vis_icon.jpg" width="25px" height="25px" /><a href="${urls.base}/visualization?vis=entity_comparison&uri=${individual.uri}">Temporal Graph</a></h3>
            </section>          
        </section>
    </section>
</#if>