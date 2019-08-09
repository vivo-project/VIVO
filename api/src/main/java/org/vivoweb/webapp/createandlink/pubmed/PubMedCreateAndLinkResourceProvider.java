/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink.pubmed;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.cornell.mannlib.vitro.webapp.utils.http.HttpClientFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.vivoweb.webapp.createandlink.Citation;
import org.vivoweb.webapp.createandlink.CreateAndLinkResourceProvider;
import org.vivoweb.webapp.createandlink.ExternalIdentifiers;
import org.vivoweb.webapp.createandlink.ResourceModel;
import org.vivoweb.webapp.createandlink.utils.HttpReader;
import org.vivoweb.webapp.createandlink.utils.StringArrayDeserializer;

import java.io.IOException;

public class PubMedCreateAndLinkResourceProvider implements CreateAndLinkResourceProvider {
    protected final Log logger = LogFactory.getLog(getClass());

    public final static String PUBMED_ID_API = "http://www.ncbi.nlm.nih.gov/pmc/utils/idconv/v1.0/?format=json&ids=";
    public final static String PUBMED_SUMMARY_API = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&retmode=json&tool=my_tool&email=my_email@example.com&id=";

    @Override
    public String normalize(String id) {
        return id.trim();
    }

    @Override
    public String getLabel() {
        return "PubMed ID";
    }

    @Override
    public ExternalIdentifiers allExternalIDsForFind(String externalId) {
        ExternalIdentifiers ids = new ExternalIdentifiers();
        ids.PubMedID = externalId;

        String json = readUrl(PUBMED_ID_API + externalId);
        if (!StringUtils.isEmpty(json)) {
            PubMedIDResponse response = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                response = objectMapper.readValue(json, PubMedIDResponse.class);
            } catch (IOException e) {
                logger.error("Unable to read JSON", e);
            }
            if (response != null && !ArrayUtils.isEmpty(response.records)) {
                ids.DOI = response.records[0].doi;
                ids.PubMedCentralID = response.records[0].pmcid;
            }
        }

        return ids;
    }

    @Override
    public String findInExternal(String id, Citation citation) {
        try {
            String json = readUrl(PUBMED_SUMMARY_API + id);
            if (StringUtils.isEmpty(json)) {
                return null;
            }

            JsonFactory factory = new JsonFactory();
            JsonParser parser  = factory.createParser(json);
            if (parser != null) {
                while (!parser.isClosed() && !id.equals(parser.getCurrentName())) {
                    JsonToken token = parser.nextToken();
                }

                if (!parser.isClosed()) {
                    // We have reached the field for our ID, but we need to be on the next token for the mapper to work
                    JsonToken token = parser.nextToken();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    PubMedSummaryResponse response = objectMapper.readValue(parser, PubMedSummaryResponse.class);
                    if (response != null) {
                        citation.title = response.title;
                        citation.authors = new Citation.Name[response.authors.length];
                        for (int idx = 0; idx < response.authors.length; idx++) {
                            citation.authors[idx] = new Citation.Name();
                            citation.authors[idx].name = normalizeAuthorName(response.authors[idx].name);
                        }
                        citation.journal = response.fulljournalname;
                        citation.volume = response.volume;
                        citation.issue = response.issue;
                        citation.pagination = response.pages;
                        if (!StringUtils.isEmpty(response.pubdate) && response.pubdate.length() >= 4) {
                            citation.publicationYear = Integer.parseInt(response.pubdate.substring(0, 4), 10);
                        }

                        citation.type = getCiteprocTypeForPubType(response.pubtype);

                        return json;
                    }
                }
            }

            return null;
        } catch (Exception e) {
            logger.error("[PMID] Error resolving PMID " + id + ", cause "+ e.getMessage());
            return null;
        }
    }

    @Override
    public ResourceModel makeResourceModel(String externalId, String externalResource) {
        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(externalResource);
            if (parser != null) {
                while (!parser.isClosed() && !externalId.equals(parser.getCurrentName())) {
                    JsonToken token = parser.nextToken();
                }

                if (!parser.isClosed()) {
                    // We have reached the field for our ID, but we need to be on the next token for the mapper to work
                    JsonToken token = parser.nextToken();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    PubMedSummaryResponse response = objectMapper.readValue(parser, PubMedSummaryResponse.class);
                    if (response != null) {
                        ResourceModel resourceModel = new ResourceModel();
                        resourceModel.PubMedID = externalId;
                        resourceModel.title = response.title;
                        resourceModel.author = new ResourceModel.NameField[response.authors.length];
                        for (int idx = 0; idx < response.authors.length; idx++) {
                            resourceModel.author[idx] = new ResourceModel.NameField();
                            if (response.authors[idx].name.lastIndexOf(' ') > 0) {
                                resourceModel.author[idx].family = response.authors[idx].name.substring(0, response.authors[idx].name.lastIndexOf(' '));
                                resourceModel.author[idx].given = response.authors[idx].name.substring(response.authors[idx].name.lastIndexOf(' ') + 1);
                            } else {
                                resourceModel.author[idx].family = response.authors[idx].name;
                            }
                        }

                        resourceModel.containerTitle = response.fulljournalname;
                        if (!StringUtils.isEmpty(response.issn)) {
                            resourceModel.ISSN = new String[1];
                            resourceModel.ISSN[0] = response.issn;
                        } else if (!StringUtils.isEmpty(response.eissn)) {
                            resourceModel.ISSN = new String[1];
                            resourceModel.ISSN[0] = response.eissn;
                        }

                        resourceModel.volume = response.volume;
                        resourceModel.issue = response.issue;
                        if (response.pages.contains("-")) {
                            int hyphen = response.pages.indexOf('-');
                            resourceModel.pageStart = response.pages.substring(0, hyphen);
                            resourceModel.pageEnd = response.pages.substring(hyphen + 1);
                        } else {
                            resourceModel.pageStart = response.pages;
                        }

                        if (!StringUtils.isEmpty(response.pubdate) && response.pubdate.length() >= 4) {
                            resourceModel.publicationDate = new ResourceModel.DateField();
                            resourceModel.publicationDate.year = Integer.parseInt(response.pubdate.substring(0, 4), 10);
                        }

                        if (response.articleids != null) {
                            for (PubMedSummaryResponse.ArticleID articleID : response.articleids) {
                                if (!StringUtils.isEmpty(articleID.value)) {
                                    if ("doi".equalsIgnoreCase(articleID.idtype)) {
                                        resourceModel.DOI = articleID.value.trim();
                                    } else if ("pmc".equalsIgnoreCase(articleID.idtype)) {
                                        resourceModel.PubMedCentralID = articleID.value.trim();
                                    } else if ("pmcid".equalsIgnoreCase(articleID.idtype)) {
                                        if (StringUtils.isEmpty(resourceModel.PubMedCentralID)) {
                                            String id = articleID.value.replaceAll(".*(PMC[0-9]+).*", "$1");
                                            if (!StringUtils.isEmpty(id)) {
                                                resourceModel.PubMedCentralID = id;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        resourceModel.type = getCiteprocTypeForPubType(response.pubtype);
                        resourceModel.publisher = response.publishername;
                        resourceModel.status = response.pubstatus;

        /*
            public DateField created;
            public String[] subject;
            public String presentedAt;
            public String[] keyword;
            public String abstractText;
         */
                        return resourceModel;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Unable to read JSON", e);
        }

        return null;
    }

    private String normalizeAuthorName(String name) {
        if (name.indexOf(',') < 0 && name.indexOf(' ') > -1) {
            int lastSpace = name.lastIndexOf(' ');
            int insertPoint = lastSpace;
            while (insertPoint > 0) {
                if (name.charAt(insertPoint - 1) == ' ') {
                    insertPoint--;
                } else {
                    break;
                }
            }

            return name.substring(0, insertPoint) + "," + name.substring(lastSpace);
        }
        return name;
    }

    private String getCiteprocTypeForPubType(String[] pubTypes) {
        if (pubTypes != null && pubTypes.length > 0) {
            for (String pubType : pubTypes) {
                switch (pubType) {
                    case "Journal Article":
                        return "article-journal";

                    case "Incunabula":
                    case "Monograph":
                    case "Textbooks":
                        return "book";

                    case "Dataset":
                        return "dataset";

                    case "Legal Cases":
                        return "legal_case";

                    case "Legislation":
                        return "legislation";

                    case "Manuscripts":
                        return "manuscript";

                    case "Maps":
                        return "map";

                    case "Meeting Abstracts":
                        return "paper-conference";

                    case "Patents":
                        return "patent";

                    case "Letter":
                        return "personal_communication";

                    case "Blogs":
                        return "post-weblog";

                    case "Review":
                        return "review";

                    case "Academic Dissertations":
                        return "thesis";
                }
            }
        }

        return "article-journal";
    }

    private String readUrl(String url) {
        try {
            HttpClient client = HttpClientFactory.getHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            return HttpReader.fromResponse(response);
        } catch (IOException e) {
        }

        return null;
    }

    private static class PubMedIDResponse {
        public String status;
        public String responseDate;
        public String request;
        public String warning;

        public PubMedIDRecord[] records;

        public static class PubMedIDRecord {
            String pmcid;
            String pmid;
            String doi;

            // Don't need versions
        }
    }

    private static class PubMedSummaryResponse {
        public String uid;
        public String pubdate;
        //public String epubdate;
        public String source;
        public NameField[] authors;
        //public String lastauthor;
        public String title;
        //public String sorttitle;
        public String volume;
        public String issue;
        public String pages;
        @JsonDeserialize(using = StringArrayDeserializer.class)
        public String[] lang;
        //public String nlmuniqueid;
        public String issn;
        public String eissn;
        @JsonDeserialize(using = StringArrayDeserializer.class)
        public String[] pubtype;
        //public String recordstatus;
        public String pubstatus;
        public ArticleID[] articleids;
        public History[] history;
        //public String[] references;
        @JsonDeserialize(using = StringArrayDeserializer.class)
        public String[] attributes;
        //public Integer pmcrefcount;
        public String fulljournalname;
        //public String elocationid;
        //public Integer viewcount;
        //public String doctype;
        //public String[] srccontriblist;
        //public String booktitle;
        //public String medium;
        //public String edition;
        //public String publisherlocation;
        public String publishername;
        //public String srcdate;
        //public String reportnumber;
        //public String availablefromurl;
        //public String locationlabel;
        //public String[] doccontriblist;
        //public String docdate;
        //public String bookname;
        public String chapter;
        //public String sortpubdate;
        //public String sortfirstauthor;
        //public String vernaculartitle;

        public static class NameField {
            public String name;
            //public String authtype;
            //public String clusterid;
        }

        public static class ArticleID {
            public String idtype;
            //public Integer idtypen;
            public String value;
        }

        public static class History {
            public String pubstatus;
            public String date;
        }
    }
}
