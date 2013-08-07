package edu.cornell.mannlib.semservices.bo;

import java.io.Serializable;

public class SparqlFeedParam extends BaseObject implements Serializable {

   /**
    *
    */
   private static final long serialVersionUID = 4602167398212576479L;
   private int sparqlFeedId;
   private String param;
   private String value;

   public SparqlFeedParam()  {
      //  default constructor
   }

   /**
    * @return the sparqlFeedId
    */
   public int getSparqlFeedId() {
      return sparqlFeedId;
   }

   /**
    * @param sparqlFeedId the sparqlFeedId to set
    */
   public void setSparqlFeedId(int sparqlFeedId) {
      this.sparqlFeedId = sparqlFeedId;
   }

   /**
    * @return the param
    */
   public String getParam() {
      return param;
   }

   /**
    * @param param the param to set
    */
   public void setParam(String param) {
      this.param = param;
   }

   /**
    * @return the value
    */
   public String getValue() {
      return value;
   }

   /**
    * @param value the value to set
    */
   public void setValue(String value) {
      this.value = value;
   }

}
