package org.vivoweb.webapp.startup;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessClassGroupDataGetterN3;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessDataGetterN3Map;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessFixedHTMLN3;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessInternalClassDataGetterN3;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessSearchFilterValuesDataGetterN3;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessSearchIndividualsDataGetterN3;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessSparqlDataGetterN3;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.FixedHTMLDataGetter;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SearchFilterValuesDataGetter;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SearchIndividualsDataGetter;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SparqlQueryDataGetter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;

public class DataGetterN3Setup implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        HashMap<String, Class> map = new HashMap<String, Class>();
        map.put(SparqlQueryDataGetter.class.getCanonicalName(), ProcessSparqlDataGetterN3.class);
        map.put(ClassGroupPageData.class.getCanonicalName(), ProcessClassGroupDataGetterN3.class);
		map.put(SearchFilterValuesDataGetter.class.getCanonicalName(), ProcessSearchFilterValuesDataGetterN3.class);
        map.put(InternalClassesDataGetter.class.getCanonicalName(), ProcessInternalClassDataGetterN3.class);
        map.put(FixedHTMLDataGetter.class.getCanonicalName(), ProcessFixedHTMLN3.class);
        map.put(SearchIndividualsDataGetter.class.getCanonicalName(), ProcessSearchIndividualsDataGetterN3.class);

        ProcessDataGetterN3Map.replaceDataGetterMap(map);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
