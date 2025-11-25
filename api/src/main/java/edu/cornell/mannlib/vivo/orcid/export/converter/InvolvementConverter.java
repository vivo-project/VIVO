package edu.cornell.mannlib.vivo.orcid.export.converter;

import java.util.Locale;
import java.util.Map;

import edu.cornell.mannlib.vivo.orcid.export.model.common.Address;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DisambiguatedOrganization;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Organization;
import edu.cornell.mannlib.vivo.orcid.export.model.involvement.InvolvementDTO;

public class InvolvementConverter {

    protected static void setCommonFields(Map<String, String> record, InvolvementDTO dto) {
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

        if (record.containsKey("institutionName")) {
            Organization organization = new Organization();
            organization.setName(record.get("institutionName"));

            if (record.containsKey("city") && record.containsKey("country")) {
                Address address = new Address();
                address.setCity(record.get("city"));
                address.setCountry(getCountryCode(record.get("country")));

                if (record.containsKey("region")) {
                    address.setRegion(record.get("region"));
                }

                organization.setAddress(address);
            }

            if (record.containsKey("ror")) {
                organization.setDisambiguatedOrganization(new DisambiguatedOrganization(
                    "https://ror.org/" + record.get("ror"),
                    "ROR"
                ));
            }

            dto.setOrganization(organization);
        }

        if (record.containsKey("urlValue")) {
            dto.setUrl(new ContentValue(record.get("urlValue")));
        }
    }

    private static String getCountryCode(String countryName) {
        String[] isoCountryCodes = Locale.getISOCountries();
        String countryCode = "";

        for (String code : isoCountryCodes) {
            Locale locale = new Locale("", code);
            String name = locale.getDisplayCountry();

            if (name.equalsIgnoreCase(countryName)) {
                countryCode = code;
                break;
            }
        }
        return countryCode;
    }
}
