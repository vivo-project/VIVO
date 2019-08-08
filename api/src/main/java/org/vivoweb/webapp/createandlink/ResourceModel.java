/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink;

public class ResourceModel {
    public String DOI;
    public String PubMedID;
    public String PubMedCentralID;
    public String[] ISSN;
    public String[] ISBN;
    public String URL;

    public NameField[] author;
    public NameField[] editor;
    public NameField[] translator;

    public String containerTitle;
    public String issue;
    public String pageStart;
    public String pageEnd;

    public DateField publicationDate;

    public String publisher;

    public String[] subject;
    public String title;
    public String type;
    public String volume;

    public String status;
    public String presentedAt;
    public String[] keyword;
    public String abstractText;

    public static class NameField {
        public String family;
        public String given;
    }

    public static class DateField {
        public Integer year;
        public Integer month;
        public Integer day;
    }
}
