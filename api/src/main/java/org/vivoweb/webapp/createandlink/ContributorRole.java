/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink;

public class ContributorRole {
    private String key;
    private String label;
    private String uri;

    public ContributorRole(String key, String label, String uri) {
        this.key = key;
        this.label = label;
        this.uri = uri;
    }

    public String getKey() { return key; }
    public String getLabel() { return label; }
    public String getUri() { return uri; }
}
