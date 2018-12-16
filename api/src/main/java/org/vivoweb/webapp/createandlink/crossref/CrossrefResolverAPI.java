/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink.crossref;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.utils.http.HttpClientFactory;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.vivoweb.webapp.createandlink.Citation;
import org.vivoweb.webapp.createandlink.CreateAndLinkUtils;
import org.vivoweb.webapp.createandlink.ResourceModel;
import org.vivoweb.webapp.createandlink.utils.HttpReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface to the CrossRef resolver
 */
public class CrossrefResolverAPI {
    protected final Log logger = LogFactory.getLog(getClass());

    // Base URL for the resolver
    private static final String CROSSREF_RESOLVER = "https://doi.org/";

    /**
     * Find the DOI in CrossRef, filling the citation object
     *
     * @param id
     * @param citation
     * @return
     */
    public String findInExternal(String id, Citation citation) {
        try {
            // Read JSON from the resolver
            String json = readJSON(CROSSREF_RESOLVER + URLEncoder.encode(id));

            if (StringUtils.isEmpty(json)) {
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            CrossrefCiteprocJSONModel jsonModel = objectMapper.readValue(json, CrossrefCiteprocJSONModel.class);
            if (jsonModel == null) {
                return null;
            }

            // Ensure that we have the correct resource
            if (!id.equalsIgnoreCase(jsonModel.DOI)) {
                return null;
            }

            // Map the fields of the resolver response to the citation object

            citation.DOI = id;
            citation.type = normalizeType(jsonModel.type);
            citation.title = jsonModel.title;
            citation.journal = jsonModel.containerTitle;

            if (jsonModel.author != null) {
                List<Citation.Name> authors = new ArrayList<>();
                for (CrossrefCiteprocJSONModel.NameField author : jsonModel.author) {
                    splitNameLiteral(author);
                    Citation.Name citationAuthor = new Citation.Name();
                    citationAuthor.name = CreateAndLinkUtils.formatAuthorString(author.family, author.given);
                    authors.add(citationAuthor);
                }
                citation.authors = authors.toArray(new Citation.Name[authors.size()]);
            }

            citation.volume = jsonModel.volume;
            citation.issue = jsonModel.issue;
            citation.pagination = jsonModel.page;
            if (citation.pagination == null) {
                citation.pagination = jsonModel.articleNumber;
            }

            citation.publicationYear = extractYearFromDateField(jsonModel.publishedPrint);
            if (citation.publicationYear == null) {
                citation.publicationYear = extractYearFromDateField(jsonModel.publishedOnline);
            }

            return json;
        } catch (Exception e) {
            logger.error("[CREF] Error resolving DOI " + id + ", cause "+ e.getMessage());
            return null;
        }
    }

    /**
     * Extract the year from the crossref JSON model
     *
     * @param date
     * @return
     */
    private Integer extractYearFromDateField(CrossrefCiteprocJSONModel.DateField date) {
        if (date == null) {
            return null;
        }

        if (ArrayUtils.isEmpty(date.dateParts)) {
            return null;
        }

        return Integer.parseInt(date.dateParts[0][0]);
    }

    /**
     *
     * @param externalResource
     * @return
     */
    public ResourceModel makeResourceModel(String externalResource) {
        if (StringUtils.isEmpty(externalResource)) {
            return null;
        }

        CrossrefCiteprocJSONModel jsonModel = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            jsonModel = objectMapper.readValue(externalResource, CrossrefCiteprocJSONModel.class);
        } catch (IOException e) {
            logger.error("Unable to read JSON", e);
        }
        if (jsonModel == null) {
            return null;
        }

        if (StringUtils.isEmpty(jsonModel.DOI)) {
            return null;
        }

        // Map the fields of the Java object to the resource model

        ResourceModel model = new ResourceModel();

        model.DOI = jsonModel.DOI;
        model.PubMedID = jsonModel.PMID;
        model.PubMedCentralID = jsonModel.PMCID;
        model.ISSN = jsonModel.ISSN;
        model.ISBN = jsonModel.ISBN;
        model.URL = jsonModel.URL;

        if (jsonModel.ISBN != null) {
            int isbnIdx = 0;
            model.ISBN = new String[jsonModel.ISBN.length];
            for (String isbn : jsonModel.ISBN) {
                if (isbn.lastIndexOf('/') > -1) {
                    isbn = isbn.substring(isbn.lastIndexOf('/') + 1);
                }

                model.ISBN[isbnIdx] = isbn;
                isbnIdx++;
            }
        }

        model.author = convertNameFields(jsonModel.author);
        model.editor = convertNameFields(jsonModel.editor);
        model.translator = convertNameFields(jsonModel.translator);

        model.containerTitle = jsonModel.containerTitle;

        model.issue = jsonModel.issue;

        if (!StringUtils.isEmpty(jsonModel.page)) {
            if (jsonModel.page.contains("-")) {
                int hyphen = jsonModel.page.indexOf('-');
                model.pageStart = jsonModel.page.substring(0, hyphen);
                model.pageEnd = jsonModel.page.substring(hyphen + 1);
            } else {
                model.pageStart = jsonModel.page;
            }
        } else if (!StringUtils.isEmpty(jsonModel.articleNumber)) {
            model.pageStart = jsonModel.articleNumber;
        }

        model.publicationDate = convertDateField(jsonModel.publishedPrint);
        if (model.publicationDate == null) {
            model.publicationDate = convertDateField(jsonModel.publishedOnline);
        }

        model.publisher = jsonModel.publisher;
        model.subject = jsonModel.subject;
        model.title = jsonModel.title;
        model.type = normalizeType(jsonModel.type);
        model.volume = jsonModel.volume;

        model.status = jsonModel.status;
        model.presentedAt = jsonModel.event;
        model.abstractText = jsonModel.abstractText;

        return model;
    }

    /**
     * Convert CiteProc name fields into resource model name fields
     *
     * @param nameFields
     * @return
     */
    private ResourceModel.NameField[] convertNameFields(CrossrefCiteprocJSONModel.NameField[] nameFields) {
        if (nameFields == null) {
            return null;
        }

        ResourceModel.NameField[] destNameFields = new ResourceModel.NameField[nameFields.length];

        for (int nameIdx = 0; nameIdx < nameFields.length; nameIdx++) {
            if (nameFields[nameIdx] != null) {
                splitNameLiteral(nameFields[nameIdx]);
                destNameFields[nameIdx] = new ResourceModel.NameField();
                destNameFields[nameIdx].family = nameFields[nameIdx].family;
                destNameFields[nameIdx].given = nameFields[nameIdx].given;
            }
        }

        return destNameFields;
    }

    /**
     * Map non-standard publication types into the CiteProc types
     *
     * @param type
     * @return
     */
    private String normalizeType(String type) {
        if (type != null) {
            switch (type.toLowerCase()) {
                case "journal-article":
                    return "article-journal";

                case "book-chapter":
                    return "chapter";

                case "proceedings-article":
                    return "paper-conference";
            }
         }

        return type;
    }

    /**
     * Split a name literal into first and last names
     *
     * @param author
     */
    private void splitNameLiteral(CrossrefCiteprocJSONModel.NameField author) {
        if (StringUtils.isEmpty(author.family)) {
            String given = null;
            if (!StringUtils.isEmpty(author.literal)) {
                if (author.literal.contains(",")) {
                    author.family = author.literal.substring(0, author.literal.indexOf(','));
                    given = author.literal.substring(author.literal.indexOf(',') + 1);
                } else if (author.literal.lastIndexOf(' ') > -1) {
                    author.family = author.literal.substring(author.literal.lastIndexOf(' ') + 1);
                    given = author.literal.substring(0, author.literal.lastIndexOf(' '));
                } else {
                    author.family = author.literal;
                }
            }

            if (StringUtils.isEmpty(author.given)) {
                author.given = given;
            }
        }
    }

    /**
     * Convert a CiteProc date field to resource model date field
     *
     * @param dateField
     * @return
     */
    private ResourceModel.DateField convertDateField(CrossrefCiteprocJSONModel.DateField dateField) {
        if (dateField != null) {
            ResourceModel.DateField resourceDate = new ResourceModel.DateField();
            if (dateField.dateParts != null && dateField.dateParts.length > 0 && dateField.dateParts[0].length > 0) {
                try {
                    resourceDate.year = Integer.parseInt(dateField.dateParts[0][0], 10);
                } catch (NumberFormatException nfe) {
                }
                if (dateField.dateParts.length > 1) {
                    try {
                        resourceDate.month = Integer.parseInt(dateField.dateParts[0][1], 10);
                    } catch (NumberFormatException nfe) {
                        switch (dateField.dateParts[0][1].toLowerCase()) {
                            case "jan":
                            case "january":
                                resourceDate.month = 1;
                                break;

                            case "feb":
                            case "february":
                                resourceDate.month = 2;
                                break;

                            case "mar":
                            case "march":
                                resourceDate.month = 3;
                                break;

                            case "apr":
                            case "april":
                                resourceDate.month = 4;
                                break;

                            case "may":
                                resourceDate.month = 5;
                                break;

                            case "jun":
                            case "june":
                                resourceDate.month = 6;
                                break;

                            case "jul":
                            case "july":
                                resourceDate.month = 7;
                                break;

                            case "aug":
                            case "august":
                                resourceDate.month = 8;
                                break;

                            case "sep":
                            case "september":
                                resourceDate.month = 9;
                                break;

                            case "oct":
                            case "october":
                                resourceDate.month = 10;
                                break;

                            case "nov":
                            case "november":
                                resourceDate.month = 11;
                                break;

                            case "dec":
                            case "december":
                                resourceDate.month = 12;
                                break;
                        }
                    }
                }
                if (dateField.dateParts.length > 2) {
                    try {
                        resourceDate.day = Integer.parseInt(dateField.dateParts[0][2], 10);
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
            return resourceDate;
        }

        return null;
    }

    /**
     * Read JSON from the URL
     * @param url
     * @return
     */
    private String readJSON(String url) {
        try {
            HttpClient client = HttpClientFactory.getHttpClient();
            HttpGet request = new HttpGet(url);

            // Content negotiate for csl / citeproc JSON
            request.setHeader("Accept", "application/vnd.citationstyles.csl+json;q=1.0");

            HttpResponse response = client.execute(request);
            return HttpReader.fromResponse(response);
        } catch (IOException e) {
        }

        return null;
    }
}
