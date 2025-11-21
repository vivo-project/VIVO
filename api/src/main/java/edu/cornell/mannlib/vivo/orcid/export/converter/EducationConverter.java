package edu.cornell.mannlib.vivo.orcid.export.converter;

import java.util.Map;

import edu.cornell.mannlib.vivo.orcid.export.model.involvement.EducationDTO;

public class EducationConverter extends InvolvementConverter {

    public static EducationDTO toOrcidModel(Map<String, String> record, String researcherOrcidId) {
        EducationDTO dto = new EducationDTO();

        setCommonFields(record, dto);

        if (record.containsKey("position")) {
            dto.setRoleTitle(record.get("position").split(":", 2)[1]);
        }

        return dto;
    }
}
