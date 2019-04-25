/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames.TBOX_ASSERTIONS;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.edit.utils.LocalNamespaceClassUtils;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
/*
 * Custom controller for menu management.  This will be replaced later once N3 Editing
 * has been successfully refactored and integrated with menu management.
 */
@WebServlet(name = "InstitutionalInternalClassController", urlPatterns = {"/processInstitutionalInternalClass"} )
public class InstitutionalInternalClassController extends FreemarkerHttpServlet {
    private static final Log log = LogFactory.getLog(InstitutionalInternalClassController.class);

    private static final String EDIT_FORM = "/processInstitutionalInternalClass";
    public final static AuthorizationRequest REQUIRED_ACTIONS = SimplePermission.MANAGE_MENUS.ACTION;
    private static final String DISPLAY_FORM = "/institutionalInternalClassForm.ftl";
    private static HashMap<String, String> localNamespaces = new HashMap<String, String>();
    private static HashMap<String, String> localNamespaceClasses = new HashMap<String, String>();
    private static final String CREATE_CLASS_PARAM = "createClass";
    private static final String REDIRECT_PAGE = "/siteAdmin";
    @Override
    protected AuthorizationRequest requiredActions(VitroRequest vreq) {
    	return REQUIRED_ACTIONS;
    }

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

    	//Based on existing of local namespaces and number of local classes present
    	//as well as command parameter, execute command

    	Map<String, Object> data = new HashMap<String,Object>();
    	//Get all local classes and namespace information
    	retrieveLocalClasses(vreq, data);
    	if(isSubmission(vreq)){
    		processSubmission(vreq, data);
    	} else if(isCreateNewClass(vreq)) {
    		//Local namespace(s) exist and user wishes to create a new class
    		//Either cmd = create new or no local classes exist at all and one must be created
    		processCreateNewClass(vreq, data);
    	} else if(isSelectExistingClass(vreq)) {
    		//Local namespace(s) exist and user can select an existing class
    		processSelectExistingClass(vreq, data);
    	}  else if(isCreateOntologies(vreq)) {
    		//Not being handled expliclity but message will display indicating
    		//no local namespaces exist and one must be created
    		processCreateOntologies(vreq, data);
    	} else {
    		log.error("Don't recognize the type of request.");
    	}
    	//Retrieve local namespaces


    	//Check if existing local namespaces

    	data.put("formUrl", vreq.getContextPath() + EDIT_FORM);
    	data.put("cancelUrl", vreq.getContextPath() + REDIRECT_PAGE);

    	//if no local namespaces, then provide message to display
    	//if existing namespace(s), then check
    	//if single namespace, retrieve all classes belonging to that local namespace
    	//if multiple namespaces, generate select list with namespaces
    	//for instertion: VClassDaoJena.insertVClass
    	//
    	if(isSubmission(vreq)){
    		return redirectToSiteAdmin();
    	}
    	return new TemplateResponseValues(DISPLAY_FORM, data);

    }

	private boolean isSubmission(VitroRequest vreq) {
		String submit = vreq.getParameter("submitForm");
		return(submit!= null && !submit.isEmpty());
	}

	private void processCreateOntologies(VitroRequest vreq, Map<String, Object> data) {
		data.put("submitAction", "");

	}

	private boolean isCreateOntologies(VitroRequest vreq) {
		//no local namespaces
		return (localNamespaces.size() == 0);

	}

	private void processCreateNewClass(VitroRequest vreq, Map<String, Object> data) {
		//this may need to be changed on the basis of how new classes interact with new ontologies
		data.put("submitAction", "Create Class");
		data.put("createNewClass", true);
	}

	private boolean isCreateNewClass(VitroRequest vreq) {
		String command = vreq.getParameter("cmd");
		if(command != null && command.equals(CREATE_CLASS_PARAM)) {
			return true;
		}
		//If local namespace exists but no classes in local namespaces, then need to enable creation of new classes
		return(localNamespaces.size() > 0 && localNamespaceClasses.size() == 0);
	}

	private void processSelectExistingClass(VitroRequest vreq, Map<String, Object> data) {
		//Check if local classes exist and use for selection
		data.put("useExistingLocalClass", true);
		data.put("submitAction", "Save");
	}

	private boolean isSelectExistingClass(VitroRequest vreq) {
		//Local namespaces exist and there are existing classes within those namespaces
		return (localNamespaces.size() > 0 && localNamespaceClasses.size() > 0);
	}



	private void retrieveLocalClasses(VitroRequest vreq, Map<String, Object> data) {
		localNamespaces = LocalNamespaceClassUtils.getLocalOntologyNamespaces(vreq);
    	//Get classes for local namespaces
    	localNamespaceClasses = LocalNamespaceClassUtils.getLocalNamespacesClasses(vreq, localNamespaces);
    	data.put("existingLocalClasses", localNamespaceClasses);
    	data.put("existingLocalNamespaces", localNamespaces);
    	String noLocalOntologiesMessage = "There are currently no local ontologies.  You must create a new ontology";
    	data.put("noLocalOntologiesMessage", noLocalOntologiesMessage);
    	if(localNamespaces.size() == 0) {
    		data.put("ontologiesExist", false);
    	}
    	else {
    		data.put("ontologiesExist", true);
    		if(localNamespaces.size() > 1) {
    			data.put("multipleLocalNamespaces", true);
    		} else {
    			data.put("multipleLocalNamespaces", false);
    			data.put("existingLocalNamespace", localNamespaces.keySet().iterator().next());
    		}
    		//Get current internal class if it exists
    		data.put("existingInternalClass", retrieveCurrentInternalClass());
    	}
    	//Place default namespace within data to pass back to template
    	String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
    	data.put("defaultNamespace", defaultNamespace);
	}


    //Process submission on submitting form
	private void processSubmission(VitroRequest vreq, Map<String, Object> data) {
		//If new class, need to generate new class
		String classUri = null;
		if(isNewClassSubmission(vreq)){
			VClass v= generateNewVClass(vreq.getParameter("localClassName"), vreq.getParameter("existingLocalNamespaces"));
			classUri = v.getURI();
			try {
				vreq.getWebappDaoFactory().getVClassDao().insertNewVClass(v);
			} catch(Exception ex) {
				log.error("Insertion of new class " + vreq.getParameter("name") + " resulted in error ", ex);
			}
		} else {
			//Existing class so get URI from that
			classUri = getExistingClassUri(vreq);
		}
		//If existing class, need to simply add a statement specifying existing class is an internal class
		if(classUri != null && !classUri.isEmpty()) {
			Model writeModel = ModelAccess.on(getServletContext()).getOntModel(TBOX_ASSERTIONS);
			writeModel.enterCriticalSection(Lock.WRITE);
			writeModel.notifyEvent(new EditEvent(null,true));
			try {
				log.debug("Should be removing these statements " + writeModel.listStatements(null,
						ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT),
						(RDFNode) null).toList().toString());
				//remove existing internal classes if there are any as assuming only one
				writeModel.removeAll(null,
								ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT),
								(RDFNode) null);
				log.debug("Are there any statements left for internal class annotation:  " + writeModel.listStatements(null,
						ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT),
						(RDFNode) null).toList().toString());
				writeModel.add(
						writeModel.createStatement(
								ResourceFactory.createResource(classUri),
								ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT),
								writeModel.createLiteral("true")));
			} catch(Exception ex) {
				log.error("Error occurred in adding statement for " + classUri + " becoming internal class", ex);
			} finally {
				writeModel.notifyEvent(new EditEvent(null,true));
				writeModel.leaveCriticalSection();
			}
		}
	}

	private VClass generateNewVClass(String newClassName, String namespace) {
		VClass newClass = new VClass();
		newClass.setName(newClassName);
		newClass.setNamespace(namespace);
		String uri = namespace + newClassName.replaceAll(" ", "");
		newClass.setURI(uri);
		//How to g
		return newClass;
	}

	private boolean isNewClassSubmission(VitroRequest vreq) {
		String localName = vreq.getParameter("localClassName");
		return (localName != null && !localName.isEmpty());
	}

	private String getExistingClassUri(VitroRequest vreq) {
		return vreq.getParameter("existingLocalClasses");

	}

	private RedirectResponseValues redirectToSiteAdmin() {
		return new RedirectResponseValues(REDIRECT_PAGE, HttpServletResponse.SC_SEE_OTHER);
	}

	//Get current internal class
	private String retrieveCurrentInternalClass() {
		String internalClassUri = "";
		Model mainModel = ModelAccess.on(getServletContext()).getOntModel(TBOX_ASSERTIONS);
		StmtIterator internalIt = mainModel.listStatements(null,
				ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT),
				(RDFNode) null);
		while(internalIt.hasNext()){
			Statement s = internalIt.nextStatement();
			//The class IS an internal class so the subject is what we're looking for
			internalClassUri = s.getSubject().getURI();
			log.debug("Found internal class uri " + internalClassUri);
		}
		return internalClassUri;
	}

}
