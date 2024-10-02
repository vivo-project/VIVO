package edu.cornell.mannlib.vitro.webapp.controller.api.dto;

import java.util.Map;
import java.util.Objects;

public class InformationContentEntityResponseUtility {

    public static void addAuthorToICE(Map<String, String> binding, InformationContentEntityResponseDTO entity) {
        if (!Objects.nonNull(binding.get("author"))) {
            return;
        }

        if (entity.authors.stream().anyMatch(author -> author.name.equals(binding.get("author")))) {
            return;
        }

        AuthorDTO author = new AuthorDTO();
        author.name = binding.getOrDefault("author", null);
        author.type = binding.getOrDefault("authorType", null);
        author.identifier = binding.getOrDefault("authorIdentifier", null);
        entity.authors.add(author);
    }

    public static void addFundingToICE(Map<String, String> binding, InformationContentEntityResponseDTO entity) {
        if (!Objects.nonNull(binding.get("funding"))) {
            return;
        }

        if (entity.fundings.stream().anyMatch(funding -> funding.equals(binding.get("funding")))) {
            return;
        }

        entity.fundings.add(binding.get("funding"));
    }

    public static void addFundersToICE(Map<String, String> binding, InformationContentEntityResponseDTO entity) {
        if (!Objects.nonNull(binding.get("funder"))) {
            return;
        }

        if (entity.funders.stream().anyMatch(funder -> funder.name.equals(binding.get("funder")))) {
            return;
        }

        FunderResponseDTO funder = new FunderResponseDTO();
        funder.name = binding.get("funder");
        funder.type = binding.getOrDefault("funderType", null);
        entity.funders.add(funder);
    }
}
