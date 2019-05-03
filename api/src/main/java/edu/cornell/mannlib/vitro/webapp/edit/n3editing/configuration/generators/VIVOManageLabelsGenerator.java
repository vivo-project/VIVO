/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

/**
 *This generator selects the actual generator to be employed based on whether the individual is a Person
 *or another individual.  Adding a label for a person relies on first/name last name information i.e. object properties.
 */
public class VIVOManageLabelsGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {
    public static Log log = LogFactory.getLog(ManageLabelsForIndividualGenerator.class);
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
    	EditConfigurationVTwo e = null;
    	String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
    	if(this.isPersonType(subjectUri, vreq)) {
    		//Generator for persons
    		e = new ManageLabelsForPersonGenerator().getEditConfiguration(vreq, session);
    	} else {
    		//Non-Person individuals
    		e = new ManageLabelsForIndividualGenerator().getEditConfiguration(vreq, session);

    	}

    	return e;

    }


    public boolean isPersonType(String subjectUri, VitroRequest vreq) {
		Boolean isPersonType = Boolean.FALSE;
		String foafPersonType = getFOAFPersonClassURI();
		List<VClass> vclasses = this.getVClasses(subjectUri, vreq);
	    if( vclasses != null ){
	    	for( VClass v: vclasses){
	    		String typeUri = v.getURI();
	    		if( foafPersonType.equals(typeUri)) {
	    			isPersonType = Boolean.TRUE;
	    			break;
	    		}
	    	}
	    }
	    return isPersonType;
	}

  //Copied from NewIndividualFormGenerator
  	//TODO: Refactor so common code can be used by both generators
  	public String getFOAFPersonClassURI() {
  		return "http://xmlns.com/foaf/0.1/Person";
  	}

  //how to get the type of the individual in question
  	public List<VClass> getVClasses(String subjectUri, VitroRequest vreq) {
  		Individual subject = EditConfigurationUtils.getIndividual(vreq, subjectUri);
  		//Get the vclasses appropriate for this subject
  		return subject.getVClasses();
  	}

}
