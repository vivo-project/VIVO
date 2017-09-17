package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessDataGetterN3Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;

public class DataGetterN3Setup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SparqlQueryDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessSparqlDataGetterN3");
        map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessClassGroupDataGetterN3");
        map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessInternalClassDataGetterN3");
        map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.FixedHTMLDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessFixedHTMLN3");
        map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SearchIndividualsDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessSearchIndividualsDataGetterN3");

        ProcessDataGetterN3Map.replaceDataGetterMap(map);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
