package edu.cornell.mannlib.semservices.bo;


import java.util.HashMap;
import java.util.Map;

public class SparqlPlugin extends BaseObject {
   private String type;
   private String name;
   private String classname;

   /**
    *
    */
   public SparqlPlugin() {
      super();
   }
   /**
    * @return the name
    */
   public String getName() {
      return name;
   }
   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
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
    * @return the classname
    */
   public String getClassname() {
      return classname;
   }
   /**
    * @param classname the classname to set
    */
   public void setClassname(String classname) {
      this.classname = classname;
   }
}
