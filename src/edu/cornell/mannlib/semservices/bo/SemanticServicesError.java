/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.bo;

public class SemanticServicesError {
   private String message;
   private String exception;
   private String severity;

   /**
    *
    */
   public SemanticServicesError() {
      super();
   }



   /**
    * @param exception
    * @param message
    * @param severity
    */
   public SemanticServicesError(String exception, String message, String severity) {
      super();
      this.exception = exception;
      this.message = message;
      this.severity = severity;
   }



   /**
    * @return the message
    */
   public String getMessage() {
      return message;
   }

   /**
    * @param message the message to set
    */
   public void setMessage(String message) {
      this.message = message;
   }

   /**
    * @return the exception
    */
   public String getException() {
      return exception;
   }

   /**
    * @param exception the exception to set
    */
   public void setException(String exception) {
      this.exception = exception;
   }

   /**
    * @return the severity
    */
   public String getSeverity() {
      return severity;
   }

   /**
    * @param severity the severity to set
    */
   public void setSeverity(String severity) {
      this.severity = severity;
   }

}
