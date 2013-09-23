<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#--The original concept javascript is service independent, i.e. all vocabulary service information is returned from the servlet
and the template itself generates the same display for all the services.  Right now we would like to show a different label
in the search results based on the service.  I am storing that information here and later we can consider how the display
can return to being independent of vocabulary service-specific display options.
These values will be passed to the javascript-->

<#assign vocabSpecificDisplay = {
"http://link.informatics.stonybrook.edu/umls":"${i18n().label_type}",
"http://aims.fao.org/aos/agrovoc/agrovocScheme":"${i18n().label_altLabels}",
"http://www.eionet.europa.eu/gemet/gemetThesaurus":"${i18n().label_type}",
"http://id.loc.gov/authorities/subjects":"${i18n().label_altLabels}"
}/>
