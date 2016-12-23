/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.semservices.bo;

public class BaseObject {
   /**
    * Simple JavaBean domain object with an id property.
    * Used as a base class for objects needing this property.
    */
   private Integer id;

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getId() {
      return id;
   }

   public boolean isNew() {
      return (this.id == null);
   }



}
