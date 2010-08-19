/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import javax.servlet.ServletContextEvent;

import net.sf.jga.fn.UnaryFunctor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.controller.EntityMergedPropertyListController;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.IndividualFiltering;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFiltersImpl;

/**
 * This adds an object to the servlet context that will be used by 
 * EntityMergedPropertiesListController to convert the individual that
 * is specified by the URI of the request to an individual that is filtered.  
 * The object is a function from an Individual to an Individual.  The original
 * Individual is wrapped in an object that will filter out any object property statements
 * to Individuals of core:Position whos date is in the past.
 * 
 * This is related to http://issues.library.cornell.edu/browse/NIHVIVO-984
 * 
 * @author bdc34
 *
 */
public class VivoMergedListFilteringSetup implements javax.servlet.ServletContextListener {
	private static final Log log = LogFactory.getLog(VivoMergedListFilteringSetup.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info("Setting up Vivo Merged List Filtering.");
		
		UnaryFunctor<Individual,Individual> fn = new UnaryFunctor<Individual,Individual>(){

			@Override
			public Individual fn(Individual subject) {			
				if( subject != null && subject.isVClass("http://xmlns.com/foaf/0.1/Organization")){
					return new IndividualFiltering(subject, new VitroFiltersImpl(){
						
	//					@Override
	//					public UnaryFunctor<Individual, Boolean> getIndividualFilter(){
	//						return new UnaryFunctor<Individual,Boolean>(){
	//							@Override
	//							public Boolean fn(Individual arg) {
	//								return ! isPast(arg);
	//							}							
	//						};						
	//					}
						
						/**
						 * This returns true if we should keep the statement, false if we should get rid of the statement
						 */
						@Override
						public UnaryFunctor<ObjectPropertyStatement, Boolean> getObjectPropertyStatementFilter(){
							return new UnaryFunctor<ObjectPropertyStatement,Boolean>(){
								@Override
								public Boolean fn(ObjectPropertyStatement arg) {
									if( "http://vivoweb.org/ontology/core#organizationForPosition".equals(arg.getPropertyURI())){
										return ! positionInPast(arg.getObject());
									}else{
										return true;
									}
								}							
							};						
						}
						
						
					});
				}else{
					return subject;
				}
			}			
		};
		
		EntityMergedPropertyListController.setMergedPropertyListFilter(fn, arg0.getServletContext());		
	}
	
	
	private boolean positionInPast(Individual ind){	
		
		if( ind.isVClass( "http://vivoweb.org/ontology/core#Position" ) ){
			
			//positionShortView.jsp does not check core:endDate
//			for(DataPropertyStatement stmt : ind.getDataPropertyStatements("http://vivoweb.org/ontology/core#endDate")){
//				 DateTime end = new DateTime( stmt.getData() );
//				 if( end.isBeforeNow() )
//					 return true; 
//			}
			
			for(DataPropertyStatement stmt : ind.getDataPropertyStatements("http://vivoweb.org/ontology/core#endYear")){
				int endYear = Integer.MAX_VALUE;
				try{
					endYear = Integer.parseInt(stmt.getData());
				}catch(NumberFormatException nfe){
					log.warn("Could not parse year: " + stmt.getData());
				}
				int nowYear = (new DateTime()).getYear();
				
				//This should use the same logic as positionShortView.jsp
				return nowYear > endYear;				
			}
		}
		return false;
	}
}
