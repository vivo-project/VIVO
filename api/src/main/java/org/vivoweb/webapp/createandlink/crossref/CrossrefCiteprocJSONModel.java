/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink.crossref;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.vivoweb.webapp.createandlink.utils.StringArrayDeserializer;

import java.util.Date;

/**
 * Note that ISSN and ISBN are arrays in Crossref, whereas Citeproc defines them to be a single value.
 *
 */
@JsonIgnoreProperties
public class CrossrefCiteprocJSONModel {
    // Crossref Specific Fields

    @JsonDeserialize(using = StringArrayDeserializer.class)
    public String[] ISSN;
    @JsonDeserialize(using = StringArrayDeserializer.class)
    public String[] ISBN;

    public DateField created;
//    public DateField deposited;
//    public DateField indexed;

//    public String member;
    public String prefix;

    @JsonProperty("article-number")
    public String articleNumber;

    @JsonProperty("published-online")
    public DateField publishedOnline;

    @JsonProperty("published-print")
    public DateField publishedPrint;

//    @JsonProperty("reference-count")
//    public Integer referenceCount;
    public Double score;
    @JsonDeserialize(using = StringArrayDeserializer.class)
    public String[] subject;
//    public String[] subtitle;

    // Standard Citeproc fields

    public String type;
    public String id; // Number?
//    public String[] categories;
    public String language;
//    public String journalAbbreviation;
//    public String shortTitle;
    public NameField[] author;
//    @JsonProperty("collection-editor")
//    public NameField[] collectionEditor;
//    public NameField[] composer;
//    @JsonProperty("container-author")
//    public NameField[] containerAuthor;
//    public NameField[] director;
    public NameField[] editor;
    @JsonProperty("editorial-director")
//    public NameField[] editorialDirector;
//    public NameField[] interviewer;
//    public NameField[] illustrator;
//    @JsonProperty("original-author")
//    public NameField[] originalAuthor;
//    public NameField[] recipient;
//    @JsonProperty("reviewed-author")
//    public NameField[] reviewedAuthor;
    public NameField[] translator;
//    public DateField accessed;
    public DateField container;
//    @JsonProperty("event-date")
//    public DateField eventDate;
    public DateField issued;
//    @JsonProperty("original-date")
//    public DateField originalDate;
    public DateField submitted;
    @JsonProperty("abstract")
    public String abstractText;
//    public String annote;
//    public String archive;
//    public String archive_location;
//    public String authority;
//    @JsonProperty("call-number")
//    public String callNumber;
//    @JsonProperty("chapter-number")
//    public String chapterNumber;
//    @JsonProperty("citation-number")
//    public String citationNumber;
//    @JsonProperty("citation-label")
//    public String citationLabel;
//    @JsonProperty("collection-number")
//    public String collectionNumber;
    @JsonProperty("container-title")
    public String containerTitle;
//    @JsonProperty("container-title-short")
//    public String containerTitleShort;
//    public String dimensions;
    public String DOI;
//    public String edition; // Integer?
    public String event;
//    @JsonProperty("event-place")
//    public String eventPlace;
//    @JsonProperty("first-reference-note-number")
//    public String firstReferenceNoteNumber;
//    public String genre;
    public String issue; // Integer?
//    public String jurisdiction;
//    public String keyword;
//    public String locator;
//    public String medium;
    public String note;
    public String number; // Integer?
//    @JsonProperty("number-of-pages")
//    public String numberOfPages;
//    @JsonProperty("number-of-volumes")
//    public String numberOfVolumes; // Integer?
//    @JsonProperty("original-publisher")
//    public String originalPublisher;
//    @JsonProperty("original-publisher-place")
//    public String originalPublisherPlace;
//    @JsonProperty("original-title")
//    public String originalTitle;
    public String page;
//    @JsonProperty("page-first")
//    public String pageFirst;
    public String PMCID;
    public String PMID;
    public String publisher;
//    @JsonProperty("publisher-place")
//    public String publisherPlace;
//    public String references;
//    @JsonProperty("reviewed-title")
//    public String reviewedTitle;
    public String scale;
    public String section;
    public String source;
    public String status;
    public String title;
//    @JsonProperty("title-short")
//    public String titleShort;
    public String URL;
    public String version;
    public String volume; // Integer?
//    @JsonProperty("year-suffix")
//    public String yearSuffix;

    public static class NameField {
        // Crossref specific fields

//        public String[] affiliation;

        // Standard Citeproc fields

        public String family;
        public String given;
//        @JsonProperty("dropping-particle")
//        public String droppingParticle;
//        @JsonProperty("non-dropping-particle")
//        public String nonDroppingParticle;
        public String suffix;
//        @JsonProperty("comma-suffix")
//        public String commaSuffix; // Number? Boolean?
//        @JsonProperty("staticOrdering")
//        public String staticOrdering; // Number? Boolean?
        public String literal;
//        @JsonProperty("parse-names")
//        public String parseNames; // Number? Boolean?
    }

    public static class DateField {
        // Crossref specific fields

        @JsonProperty("date-time")
        public Date dateTime;
//        public Long timestamp;

        // Standard Citeproc fields

        @JsonProperty("date-parts")
        public String[][] dateParts; // Number?
//        public String season; // Number?
//        public String circa; // Number? Boolean?
        public String literal;
//        public String raw;
    }
}
