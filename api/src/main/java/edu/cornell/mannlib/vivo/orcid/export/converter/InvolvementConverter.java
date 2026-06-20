package edu.cornell.mannlib.vivo.orcid.export.converter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.neovisionaries.i18n.CountryCode;
import edu.cornell.mannlib.vitro.webapp.controller.api.sparqlquery.InvalidQueryTypeException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.utils.http.AcceptHeaderParsingException;
import edu.cornell.mannlib.vitro.webapp.utils.http.NotAcceptableException;
import edu.cornell.mannlib.vivo.orcid.export.OrcidExportDataLoader;
import edu.cornell.mannlib.vivo.orcid.export.OrcidExportQueries;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Address;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DisambiguatedOrganization;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Organization;
import edu.cornell.mannlib.vivo.orcid.export.model.involvement.InvolvementDTO;

public class InvolvementConverter {

    protected static void setCommonFields(Map<String, String> record, InvolvementDTO dto,
                                          OrcidExportDataLoader dataLoader) {
        if (record.containsKey("department")) {
            dto.setDepartmentName(record.get("department"));
        }

        if (record.containsKey("startDate")) {
            String[] dateSections = record.get("startDate").split("T")[0].split("-");
            dto.setStartDate(
                new DateDTO(
                    new ContentValue(dateSections[0]),
                    new ContentValue(dateSections[1]),
                    new ContentValue(dateSections[2])
                )
            );
        }

        if (record.containsKey("endDate")) {
            String[] dateSections = record.get("endDate").split("T")[0].split("-");
            dto.setEndDate(
                new DateDTO(
                    new ContentValue(dateSections[0]),
                    new ContentValue(dateSections[1]),
                    new ContentValue(dateSections[2])
                )
            );
        }

        setInstitutionInformation(record, dto, dataLoader);

        if (record.containsKey("urlValue")) {
            dto.setUrl(new ContentValue(record.get("urlValue")));
        }
    }

    private static void setInstitutionInformation(Map<String, String> record, InvolvementDTO dto,
                                                  OrcidExportDataLoader dataLoader) {
        if (record.containsKey("institutionName")) {
            Organization organization = new Organization();
            organization.setName(record.get("institutionName"));

            String queryString = String.format(
                OrcidExportQueries.loadQuery("find_super_org_units.sparql"),
                record.get("institution")
            );

            if (queryString.isEmpty()) {
                return;
            }

            try {
                List<Map<String, String>> superOUBindings = dataLoader.runSparqlQuery(queryString);

                Address address = null;
                DisambiguatedOrganization disambiguatedOrganization = null;

                for (Map<String, String> binding : superOUBindings) {
                    if (address == null && binding.containsKey("city") && binding.containsKey("country")) {
                        address = new Address();
                        address.setCity(binding.get("city"));
                        address.setCountry(
                            getCountryCode(binding.get("country"))
                        );

                        if (binding.containsKey("region")) {
                            address.setRegion(binding.get("region"));
                        }
                    }

                    if (disambiguatedOrganization == null && binding.containsKey("ror")) {
                        disambiguatedOrganization =
                            new DisambiguatedOrganization(
                                "https://ror.org/" +
                                    binding.get("ror")
                                        .replace("http://ror.org/", "")
                                        .replace("https://ror.org/", ""),
                                "ROR"
                            );
                    }

                    if (address != null && disambiguatedOrganization != null) {
                        break;
                    }
                }

                if (address != null) {
                    organization.setAddress(address);
                }

                if (disambiguatedOrganization != null) {
                    organization.setDisambiguatedOrganization(
                        disambiguatedOrganization
                    );
                }

            } catch (InvalidQueryTypeException
                     | NotAcceptableException
                     | AcceptHeaderParsingException
                     | RDFServiceException
                     | IOException e) {
                throw new RuntimeException(e);
            }

            dto.setOrganization(organization);
        }
    }

    private static String getCountryCode(String countryName) {
        if (countryName == null || countryName.trim().isEmpty()) {
            return "";
        }

        CountryCode code = CountryCode.findByName("(?i)" + Pattern.quote(countryName.trim()))
            .stream()
            .findFirst()
            .orElse(null);

        if (code != null) {
            return code.getAlpha2();
        }

        CountryCode byCode = CountryCode.getByCode(countryName.trim(), false);
        return byCode != null ? byCode.getAlpha2() : "";
    }
}
