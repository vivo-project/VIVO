/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.bo;

import java.util.ArrayList;
import java.util.List;

public class Concept {

   private String definedBy;
   private String conceptId;
   private String bestMatch;
   private String label;
   private String type;
   private String definition;
   private String uri;
   private String schemeURI;
   private List<String> broaderURIList;
   private List<String> narrowerURIList;
   private List<String> exactMatchURIList;
   private List<String> closeMatchURIList;
   private List<String> altLabelList;
   
   /**
    * default constructor
    */
   public Concept() {
      this.broaderURIList = new ArrayList<String>();
      this.narrowerURIList = new ArrayList<String>();
      this.exactMatchURIList = new ArrayList<String>();
      this.closeMatchURIList = new ArrayList<String>();
   }
   
   /**
    * @return the conceptId
    */
   public String getConceptId() {
      return conceptId;
   }
   /**
    * @param conceptId the conceptId to set
    */
   public void setConceptId(String conceptId) {
      this.conceptId = conceptId;
   }
   /**
    * @return the label
    */
   public String getLabel() {
      return label;
   }
   /**
    * @param label the label to set
    */
   public void setLabel(String label) {
      this.label = label;
   }
   /**
    * @return the type
    */
   public String getType() {
      return type;
   }
   /**
    * @param type the type to set
    */
   public void setType(String type) {
      this.type = type;
   }
   /**
    * @return the definition
    */
   public String getDefinition() {
      return definition;
   }
   /**
    * @param definition the definition to set
    */
   public void setDefinition(String definition) {
      this.definition = definition;
   }
   /**
    * @return the uri
    */
   public String getUri() {
      return uri;
   }
   /**
    * @param uri the uri to set
    */
   public void setUri(String uri) {
      this.uri = uri;
   }
   /**
    * @return the definedBy
    */
   public String getDefinedBy() {
      return definedBy;
   }
   /**
    * @param definedBy the definedBy to set
    */
   public void setDefinedBy(String definedBy) {
      this.definedBy = definedBy;
   }
   /**
    * @return the schemeURI
    */
   public String getSchemeURI() {
      return schemeURI;
   }
   /**
    * @param schemeURI the schemeURI to set
    */
   public void setSchemeURI(String schemeURI) {
      this.schemeURI = schemeURI;
   }
   /**
    * @return the bestMatch
    */
   public String getBestMatch() {
      return bestMatch;
   }
   /**
    * @param bestMatch the bestMatch to set
    */
   public void setBestMatch(String bestMatch) {
      this.bestMatch = bestMatch;
   }
public List<String> getBroaderURIList() {
	return broaderURIList;
}
public void setBroaderURIList(List<String> broaderURIList) {
	this.broaderURIList = broaderURIList;
}
public List<String> getNarrowerURIList() {
	return narrowerURIList;
}
public void setNarrowerURIList(List<String> narrowerURIList) {
	this.narrowerURIList = narrowerURIList;
}
public List<String> getExactMatchURIList() {
	return exactMatchURIList;
}
public void setExactMatchURIList(List<String> exactMatchURIList) {
	this.exactMatchURIList = exactMatchURIList;
}
public List<String> getCloseMatchURIList() {
	return closeMatchURIList;
}
public void setCloseMatchURIList(List<String> closeMatchURIList) {
	this.closeMatchURIList = closeMatchURIList;
}

public List<String> getAltLabelList() {
	return altLabelList;
}

public void setAltLabelList(List<String> altLabelList) {
	this.altLabelList = altLabelList;
}

}
