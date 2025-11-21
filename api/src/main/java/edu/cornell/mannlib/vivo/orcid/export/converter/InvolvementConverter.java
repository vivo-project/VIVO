package edu.cornell.mannlib.vivo.orcid.export.converter;

import java.util.Map;

import edu.cornell.mannlib.vivo.orcid.export.model.common.Address;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
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

            Address address = new Address();
            if (record.containsKey("city")) {
                address.setCity(record.get("city"));
            }
            if (record.containsKey("region")) {
                address.setRegion(record.get("region"));
            }
            if (record.containsKey("country")) {
                address.setCountry(record.get("country"));
            }
            organization.setAddress(address);

            dto.setOrganization(organization);
        }

        if (record.containsKey("urlValue")) {
            dto.setUrl(new ContentValue(record.get("urlValue")));
        }
    }
}
