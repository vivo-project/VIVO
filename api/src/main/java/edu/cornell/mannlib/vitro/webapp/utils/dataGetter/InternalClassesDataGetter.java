/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.utils.dataGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;

/**
 * This will pass these variables to the template:
 * classGroupUri: uri of the classgroup associated with this page.
 * vClassGroup: a data structure that is the classgroup associated with this page.
 */
public class InternalClassesDataGetter extends IndividualsForClassesDataGetter{
    private static final Log log = LogFactory.getLog(InternalClassesDataGetter.class);

    /**
     * Constructor with display model and data getter URI that will be called by reflection.
     */
    public InternalClassesDataGetter(VitroRequest vreq, Model displayModel, String dataGetterURI){
        super(vreq, displayModel, dataGetterURI);
    }


    //Use different template name for internal class template
    @Override
    protected void setTemplateName() {
    	super.restrictClassesTemplateName = "internalClass";
    }

    //Retrieve classes and check whether or not page to be filtered by internal class only
    @Override
    protected Map<String, Object> getClassIntersectionsMap(Model displayModel) {

    	Map<String, Object> classesAndRestrictions = new HashMap<String, Object>();
    	QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("dataGetterUri", ResourceFactory.createResource(this.dataGetterURI));
        List<String> classes = new ArrayList<String>();

        displayModel.enterCriticalSection(false);
        try{
        	Query individualsForClassesInternalQuery = QueryFactory.create(individualsForClassesInternalQueryString);
            QueryExecution qexec = QueryExecutionFactory.create( individualsForClassesInternalQuery, displayModel , initialBindings);
            try{
                ResultSet resultSet = qexec.execSelect();
                while(resultSet.hasNext()){
                    QuerySolution soln = resultSet.next();
                    String dg = DataGetterUtils.nodeToString(soln.get("dg"));
                    classes.add(DataGetterUtils.nodeToString(soln.get("class")));
                    //node to string will convert null to empty string
                    String isInternal = DataGetterUtils.nodeToString(soln.get("isInternal"));
                    if(!isInternal.isEmpty()) {
                    	log.debug("Internal value is "+ isInternal);
                    	//Retrieve and add internal class
                    	classesAndRestrictions.put("isInternal", isInternal);
                    }
                }

                if( classes.size() == 0 ){
                    log.debug("No classes  defined in display model for "+ this.dataGetterURI);
                    return null;
                }
                classesAndRestrictions.put("classes", classes);
                return classesAndRestrictions;
            }finally{
                qexec.close();
            }
        }finally{
            displayModel.leaveCriticalSection();
        }
	}


    //Retrieve current internal class uri to restrict by
	@Override
	protected List<String> retrieveRestrictClasses(
			ServletContext context, Map<String, Object> classIntersectionsMap) {
		List<String> restrictClasses = new ArrayList<String>();
		String internalClass = (String) classIntersectionsMap.get("isInternal");
		//if internal class restriction specified and is true
		if(internalClass != null && internalClass.equals("true")) {
			//Get internal class
			Model mainModel = ModelAccess.on(context).getOntModel(ModelNames.TBOX_ASSERTIONS);
			StmtIterator internalIt = mainModel.listStatements(null, ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT), (RDFNode) null);
			//Checks for just one statement
			if(internalIt.hasNext()){
				Statement s = internalIt.nextStatement();
				//The class IS an internal class so the subject is what we're looking for
				String internalClassUri = s.getSubject().getURI();
				log.debug("Found internal class uri " + internalClassUri);
				restrictClasses.add(internalClassUri);
			}
		}

		return restrictClasses;
	}

	@Override
    public String getType(){
        return DataGetterUtils.generateDataGetterTypeURI(InternalClassesDataGetter.class.getName());
    }

    static final protected String individualsForClassesInternalQueryString =
    	DataGetterUtils.prefixes + "\n" +
    	 "SELECT?class ?isInternal WHERE {\n" +
         " ?dataGetterUri <" + DisplayVocabulary.GETINDIVIDUALS_FOR_CLASS + "> ?class . \n" +
         " OPTIONAL {  ?dataGetterUri <"+ DisplayVocabulary.RESTRICT_RESULTS_BY_INTERNAL + "> ?isInternal } .\n" +
         "} \n" ;

}
