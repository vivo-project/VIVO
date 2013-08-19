/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.exceptions;

public class ConceptsNotFoundException extends Exception {
   /**
    * An exception that indicates a service could not find a Concept
    */
   private static final long serialVersionUID = -4729465393290022840L;
   public ConceptsNotFoundException() { }
   public ConceptsNotFoundException(String message) { super(message); }
   public ConceptsNotFoundException(Throwable cause) { super(cause); }
   public ConceptsNotFoundException(String message, Throwable cause) { super(message, cause); }

}
