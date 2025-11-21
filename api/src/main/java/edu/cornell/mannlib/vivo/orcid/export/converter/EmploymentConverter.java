package edu.cornell.mannlib.vivo.orcid.export.converter;

import java.util.Map;

import edu.cornell.mannlib.vivo.orcid.export.model.involvement.EmploymentDTO;

public class EmploymentConverter extends InvolvementConverter {

    public static EmploymentDTO toOrcidModel(Map<String, String> record, String researcherOrcidId) {
        EmploymentDTO dto = new EmploymentDTO();

        setCommonFields(record, dto);

        if (record.containsKey("position")) {
            dto.setRoleTitle(record.get("position"));
        }

        return dto;
    }
}
