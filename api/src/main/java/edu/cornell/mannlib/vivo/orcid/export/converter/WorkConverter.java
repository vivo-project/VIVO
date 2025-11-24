package edu.cornell.mannlib.vivo.orcid.export.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static WorkDTO toOrcidModel(Map<String, String> record, String researcherOrcidId) {
        WorkDTO dto = new WorkDTO();

        if (record.containsKey("resourceLabel")) {
            dto.setTitle(new Title(new ContentValue(record.get("resourceLabel")), null));
        }

        if (record.containsKey("journalLabel")) {
            dto.setJournalTitle(new ContentValue(record.get("journalLabel")));
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
        } else {
            dto.setType(getWorkType(record.get("workType")));
        }

        // TODO: Testing data
        ExternalIds externalIds = new ExternalIds();
        externalIds.setExternalId(new ArrayList<>());
        externalIds.getExternalId().add(
            new ExternalId(
                "doi",
                "10.1087/20120404",
                new ContentValue("https://doi.org/10.1087/20120404"),
                "part-of")
        );
        externalIds.getExternalId().add(
            new ExternalId(
                "doi",
                "work:doi",
                new ContentValue("http://orcid.org"),
                "self")
        );

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
            List<String> authorNames = Arrays.asList(record.get("authors").split("; "));
            List<String> authorRoles = Arrays.asList(record.get("authorRoles").split("; "));
            List<String> authorRanks = Arrays.asList(record.get("authorRanks").split("; "));
            List<String> orcidIds = Arrays.asList(record.get("authorOrcidIds").split("; "));
            List<String> emails = Arrays.asList(record.get("authorEmails").split("; "));

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
                        orcidIds.get(i).replace("http", "https"),
                        orcidIds.get(i).replace("http://orcid.org/", ""),
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

    private static String getWorkType(String type) {
        if (type.endsWith("Book")) {
            return "book";
        } else if (type.endsWith("Chapter")) {
            return "book-chapter";
        } else if (type.endsWith("ConferencePaper")) {
            return "conference-output";
        } else if (type.endsWith("Slideshow")) {
            return "conference-presentation";
        } else if (type.endsWith("ConferencePoster")) {
            return "conference-poster";
        } else if (type.endsWith("Proceedings")) {
            return "conference-proceedings";
        } else if (type.endsWith("AcademicArticle")) {
            return "journal-article";
        } else if (type.endsWith("Article")) {
            return "preprint";
        } else if (type.endsWith("Thesis")) {
            return "dissertation-thesis";
        } else if (type.endsWith("WorkingPaper")) {
            return "working-paper";
        } else if (type.endsWith("Document")) {
            return "other";
        } else if (type.endsWith("BlogPosting")) {
            return "blog-post";
        } else if (type.endsWith("ReferenceSource")) {
            return "dictionary-entry"; //encyclopedia-entry
        } else if (type.endsWith("Report")) {
            return "report";
        } else if (type.endsWith("Speech")) {
            return "public-speech";
        } else if (type.endsWith("Image")) {
            return "image";
        } else if (type.endsWith("Score")) {
            return "musical-composition";
        } else if (type.endsWith("AudioDocument")) {
            return "sound";
        } else if (type.endsWith("Map")) {
            return "cartographic-material";
        } else if (type.endsWith("CaseStudy")) {
            return "clinical-study";
        } else if (type.endsWith("Dataset")) {
            return "data-set";
        } else if (type.endsWith("Patent")) {
            return "patent";
        } else if (type.endsWith("EditedBook")) {
            return "edited-book";
        } else if (type.endsWith("Manual")) {
            return "manual";
        }

        // magazine-article newspaper-article
        return "Other dissemination output";
    }
}
