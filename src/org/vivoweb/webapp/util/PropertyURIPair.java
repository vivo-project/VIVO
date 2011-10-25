
/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.util;

public class PropertyURIPair {
	private String propURI = null;
	private String inversePropURI = null;
	
	public PropertyURIPair(){};
	
    public PropertyURIPair(String propURI, String inversePropURI) {
    	this.propURI = propURI;
    	this.inversePropURI = inversePropURI;
    }
	
	public String getPropURI() {
		return propURI;
	}
	
	public void setPropURI(String propURI) {
        this.propURI = propURI;
	}
	
	public String getInversePropURI() {
		return inversePropURI;
	}
	
	public void setInversePropURI(String inversePropURI) {
        this.inversePropURI = inversePropURI;
	}		
}
