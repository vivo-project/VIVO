/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink;

public class Citation {
    public String externalId;
    public String externalProvider;
    public String externalResource;

    public String vivoUri;

    public String type;
    public String typeUri;
    public String title;
    public Name[] authors;
    public String journal;
    public String volume;
    public String issue;
    public String pagination;
    public Integer publicationYear;
    public String DOI;

    public boolean alreadyClaimed = false;
    public boolean showError = false;

    public String getExternalId() { return externalId; }
    public String getExternalProvider() { return externalProvider; }
    public String getExternalResource() { return externalResource; }

    public String getVivoUri() { return vivoUri; }

    public String getType() { return type; }
    public String getTypeUri() { return typeUri; }
    public String getTitle() { return title; }
    public Name[] getAuthors() {
        return authors;
    }
    public String getJournal() {
        return journal;
    }
    public String getVolume() {
        return volume;
    }
    public String getIssue() {
        return issue;
    }
    public String getPagination() {
        return pagination;
    }
    public Integer getPublicationYear() {
        return publicationYear;
    }

    public String getDOI() {
        return DOI;
    }

    public boolean getAlreadyClaimed() { return alreadyClaimed; }

    public boolean getShowError() { return showError; }

    public static class Name {
        public String name;

        public boolean linked = false;
        public boolean proposed = false;

        public String getName() {
            return name;
        }

        public boolean getLinked() {
            return linked;
        }
        public boolean getProposed() {
            return proposed;
        }
    }
}
