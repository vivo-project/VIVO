package edu.cornell.mannlib.vivo.orcid.export.converter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ExternalId;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ExternalIds;
import edu.cornell.mannlib.vivo.orcid.export.model.work.Contributor;
import edu.cornell.mannlib.vivo.orcid.export.model.work.ContributorAttributes;
import edu.cornell.mannlib.vivo.orcid.export.model.work.ContributorOrcid;
import edu.cornell.mannlib.vivo.orcid.export.model.work.Contributors;
import edu.cornell.mannlib.vivo.orcid.export.model.work.Title;
import edu.cornell.mannlib.vivo.orcid.export.model.work.WorkDTO;

public class WorkConverter {

    private static final Map<String, String> WORK_TYPE_MAPPING =
        loadWorkTypeMappings();


    public static WorkDTO toOrcidModel(Map<String, String> record) {
        WorkDTO dto = new WorkDTO();

        if (record.containsKey("resourceLabel")) {
            dto.setTitle(new Title(new ContentValue(record.get("resourceLabel")), null));
        }

        if (record.containsKey("journalLabel")) {
            dto.setJournalTitle(new ContentValue(record.get("journalLabel")));
        }

        if (record.containsKey("bookTitle")) {
            dto.setJournalTitle(new ContentValue(record.get("bookTitle")));
        }

        if (record.containsKey("abstract")) {
            dto.setShortDescription(record.get("abstract"));
        }

        if (record.containsKey("publicationDate")) {
            String[] dateSections = record.get("publicationDate").split("T")[0].split("-");
            dto.setPublicationDate(
                new DateDTO(
                    new ContentValue(dateSections[0]),
                    new ContentValue(dateSections[1]),
                    new ContentValue(dateSections[2])
                )
            );
        }

        if (record.containsKey("urlValue")) {
            dto.setUrl(new ContentValue(record.get("urlValue")));
        }

        setAuthorInformation(record, dto);

        if (record.containsKey("performance")) {
            dto.setType("artistic-performance");
        } else if (record.containsKey("magazine")) {
            dto.setType("magazine-article");
        } else if (record.containsKey("newspaper")) {
            dto.setType("newspaper-article");
        } else {
            dto.setType(getWorkType(record.get("workType")));
        }

        ExternalIds externalIds = new ExternalIds();
        externalIds.setExternalId(new ArrayList<>());

        externalIds.getExternalId().add(
            new ExternalId(
                "source-work-id",
                record.get("resource"),
                new ContentValue(record.get("resource")),
                "self")
        );

        if (record.containsKey("doi")) {
            externalIds.getExternalId().add(
                new ExternalId(
                    "doi",
                    record.get("doi"),
                    new ContentValue("https://doi.org/" + record.get("doi")),
                    "self")
            );
        }

        if (record.containsKey("pmid")) {
            externalIds.getExternalId().add(
                new ExternalId(
                    "pmid",
                    record.get("pmid"),
                    new ContentValue("https://pubmed.ncbi.nlm.nih.gov/" + record.get("pmid")),
                    "self")
            );
        }

        if (record.containsKey("issn") || record.containsKey("eissn")) {
            String issn = record.getOrDefault("eissn", null);
            if (issn == null) {
                issn = record.get("issn");
            }

            externalIds.getExternalId().add(
                new ExternalId(
                    "issn",
                    issn,
                    new ContentValue("https://portal.issn.org/resource/ISSN/" + issn),
                    "self")
            );
        }

        if (record.containsKey("isbn10") || record.containsKey("isbn13")) {
            String isbn = record.getOrDefault("isbn10", null);
            if (isbn == null) {
                isbn = record.get("isbn13");
            }

            externalIds.getExternalId().add(
                new ExternalId(
                    "isbn",
                    isbn,
                    new ContentValue("https://www.worldcat.org/isbn/" + isbn),
                    "self")
            );
        }

        dto.setExternalIds(externalIds);

        return dto;
    }

    private static String getContributionType(String type) {
        if (type.equals("http://vivoweb.org/ontology/core#Authorship")) {
            return "author";
        } else if (type.equals("http://vivoweb.org/ontology/core#Editorship")) {
            return "editor";
        }

        return "";
    }

    private static void setAuthorInformation(Map<String, String> record, WorkDTO dto) {
        if (record.containsKey("authors")) {
            List<String> authorNames = splitOrEmpty(record.get("authors"));
            List<String> authorRoles = splitOrEmpty(record.get("authorRoles"));
            List<String> authorRanks = splitOrEmpty(record.get("authorRanks"));
            List<String> orcidIds = splitOrEmpty(record.get("authorOrcidIds"));
            List<String> emails = splitOrEmpty(record.get("authorEmails"));

            if (authorNames.size() != authorRoles.size() || authorNames.size() != authorRanks.size() ||
                authorNames.size() != orcidIds.size() || authorNames.size() != emails.size()) {
                return;
            }

            List<Contributor> contributors = new ArrayList<>();
            AtomicInteger index = new AtomicInteger(0);

            authorNames.forEach(authorName -> {
                int i = index.getAndIncrement();

                Contributor contributor = new Contributor();
                contributor.setCreditName(new ContentValue(authorName));
                contributor.setContributorEmail(emails.get(i).equals("NONE") ? null : emails.get(i));

                if (!orcidIds.get(i).equals("NONE")) {
                    contributor.setContributorOrcid(new ContributorOrcid(
                        orcidIds.get(i).replaceFirst("^http://", "https://"),
                        orcidIds.get(i)
                            .replace("http://orcid.org/", "")
                            .replace("https://orcid.org/", ""),
                        "orcid.org"
                    ));
                }

                contributor.setContributorAttributes(new ContributorAttributes(
                    authorRanks.get(i).equals("1") ? "first" : "additional",
                    getContributionType(authorRoles.get(i))
                ));

                contributors.add(contributor);
            });

            dto.setContributors(new Contributors(contributors));
        }
    }

    private static List<String> splitOrEmpty(String value) {
        return value == null
            ? Collections.emptyList()
            : Arrays.asList(value.split("; "));
    }

    private static String getWorkType(String type) {
        if (type == null) {
            return "other";
        }

        for (Map.Entry<String, String> entry : WORK_TYPE_MAPPING.entrySet()) {
            if (type.equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "other";
    }

    private static Map<String, String> loadWorkTypeMappings() {
        try (InputStream is = WorkConverter.class
            .getClassLoader()
            .getResourceAsStream("json/work-type-mapping.json")
        ) {
            if (is == null) {
                throw new RuntimeException("Mapping file not found");
            }

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(
                is,
                new TypeReference<Map<String, String>>() {
                }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load work type mappings", e);
        }
    }
}
