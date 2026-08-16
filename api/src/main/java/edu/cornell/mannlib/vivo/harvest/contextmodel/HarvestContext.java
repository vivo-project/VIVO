package edu.cornell.mannlib.vivo.harvest.contextmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportModule;

public class HarvestContext {

    public static final String DEFAULT_DISPLAY_LANGUAGE = "en-US";

    public static List<ExportModule> modules = new ArrayList<>();

    public static String logFileLocation;

    public static boolean configured = false;

    public static final List<String> REQUIRED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList("harvester.directory", "harvester.configuration", "workflow.log.directory"));


    public static void resolveLabelsBasedOnLocale(VitroRequest vreq) {
        String locale = vreq.getLocale().toLanguageTag();

        HarvestContext.modules.forEach(module -> {
            module.setResolvedDescription(
                module.getDescription()
                    .getOrDefault(locale, module.getDescription().get(HarvestContext.DEFAULT_DISPLAY_LANGUAGE)));

            module.getParameters().forEach(param -> {
                param.setResolvedName(
                    param.getName().getOrDefault(locale, param.getName().get(HarvestContext.DEFAULT_DISPLAY_LANGUAGE))
                );

                if (param.getSubfields() != null && !param.getSubfields().isEmpty()) {
                    param.getSubfields().forEach(subField ->
                        subField.setResolvedName(
                            subField.getName()
                                .getOrDefault(locale, subField.getName().get(HarvestContext.DEFAULT_DISPLAY_LANGUAGE))
                        ));
                }
            });
        });
    }
}
