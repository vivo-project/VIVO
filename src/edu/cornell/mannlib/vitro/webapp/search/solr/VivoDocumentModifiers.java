/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;

import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;

public class VivoDocumentModifiers implements javax.servlet.ServletContextListener{
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        ServletContext context = sce.getServletContext();
        
        Dataset dataset = DatasetFactory.create(ModelContext.getJenaOntModel(context));
        OntModel jenaOntModel = ModelContext.getJenaOntModel(context);
        
        /* put DocumentModifiers into servlet context for use later in startup by SolrSetup */        
        
        List<DocumentModifier> modifiers = new ArrayList<DocumentModifier>();                                        
        modifiers.add(new CalculateParameters(dataset));        //
        modifiers.add(new VivoAgentContextNodeFields(jenaOntModel));
        modifiers.add(new VivoInformationResourceContextNodeFields(jenaOntModel));
        
        context.setAttribute("DocumentModifiers", modifiers);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing.        
    }    
}
