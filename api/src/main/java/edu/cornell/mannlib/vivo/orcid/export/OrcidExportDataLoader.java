package edu.cornell.mannlib.vivo.orcid.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.controller.api.sparqlquery.SparqlQueryApiExecutor;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vivo.orcid.export.converter.EducationConverter;
import edu.cornell.mannlib.vivo.orcid.export.converter.EmploymentConverter;
import edu.cornell.mannlib.vivo.orcid.export.converter.WorkConverter;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Address;
import edu.cornell.mannlib.vivo.orcid.export.model.common.BaseEntityDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Organization;
import edu.cornell.mannlib.vivo.orcid.export.model.involvement.InvolvementDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.work.WorkDTO;
import edu.cornell.mannlib.vivo.orcid.util.OrcidExternalOperationsUtil;
import edu.cornell.mannlib.vivo.orcid.util.OrcidInternalOperationsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrcidExportDataLoader {

    private static final Log log = LogFactory.getLog(OrcidExportDataLoader.class);

    private final RDFService rdfService;

    private final int BATCH_SIZE = 10;

    private final int MAX_RETRIES = 1;


    public OrcidExportDataLoader(RDFService rdfService) {
        this.rdfService = rdfService;
    }

    public static String getSparqlQueryResponse(SparqlQueryApiExecutor core) throws IOException, RDFServiceException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        core.executeAndFormat(outputStream);

        String sparqlResponse = outputStream.toString("UTF-8");

        outputStream.close();

        return sparqlResponse;
    }

    public static List<Map<String, String>> parseBindings(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        JsonNode bindingsNode = rootNode.path("results").path("bindings");

        List<Map<String, String>> recordsList = new ArrayList<>();

        for (JsonNode bindingNode : bindingsNode) {
            Map<String, String> recordMap = new HashMap<>();

            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = bindingNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIterator.next();

                String fieldName = field.getKey();
                String fieldValue = field.getValue().path("value").asText();

                recordMap.put(fieldName, fieldValue);
            }

            recordsList.add(recordMap);
        }

        return recordsList;
    }

    public void exportSetForIndividual(String individualUri, ExportSet exportSet, String orcidId, String accessToken,
                                       boolean sandboxed) {
        try {
            boolean shouldFetch = true;
            String lastFetchedResourceUri = "";

            while (shouldFetch) {
                String queryString =
                    String.format(getQueryForExportSet(exportSet), individualUri, lastFetchedResourceUri, BATCH_SIZE);

                if (queryString.isEmpty()) {
                    return;
                }

                SparqlQueryApiExecutor core =
                    SparqlQueryApiExecutor.instance(rdfService, queryString, "application/sparql-results+json");

                String sparqlQueryResponse = getSparqlQueryResponse(core);

                List<Map<String, String>> bindings = parseBindings(sparqlQueryResponse);
                if (bindings.isEmpty()) {
                    return;
                }

                Method conversionMethod = getConversionMethod(exportSet);
                if (conversionMethod == null) {
                    return; // should never happen
                }

                for (Map<String, String> binding : bindings) {
                    String resourceUri = binding.get("resource");
                    lastFetchedResourceUri = resourceUri;

                    boolean alreadyPushed = OrcidInternalOperationsUtil.wasResourcePushedInPast(resourceUri);

                    try {
                        Object record = conversionMethod.invoke(null, binding);

                        boolean tryPush = isRecordValid(record);
                        int retries = 0;
                        boolean isRetryScenario = alreadyPushed;

                        while (tryPush && retries <= MAX_RETRIES) {
                            String updateCode =
                                OrcidExternalOperationsUtil.pushToOrcid(orcidId, getResourceEndpoint(exportSet), record,
                                    accessToken, alreadyPushed, resourceUri, sandboxed);

                            if (!alreadyPushed && updateCode != null) {
                                OrcidInternalOperationsUtil.setPushed(resourceUri, true);
                                OrcidInternalOperationsUtil.setOrcidUpdateCode(resourceUri, updateCode);
                                tryPush = false;
                            } else if (isRetryScenario && updateCode != null && updateCode.equals("RETRY")) {
                                alreadyPushed = false;
                                isRetryScenario = false;
                                ((BaseEntityDTO) record).setPutCode(null);
                                retries++;
                            } else {
                                tryPush = false;
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("Error while converting to ORCID entity: " + e.getMessage());
                    }
                }

                shouldFetch = bindings.size() == BATCH_SIZE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getQueryForExportSet(ExportSet exportSet) {
        switch (exportSet) {
            case WORKS:
                return OrcidExportQueries.FIND_ALL_WORKS;
            case EDUCATION:
                return OrcidExportQueries.FIND_ALL_EDUCATION;
            case EMPLOYMENTS:
                return OrcidExportQueries.FIND_ALL_EMPLOYMENTS;
        }

        return ""; // Should never happen
    }

    private String getResourceEndpoint(ExportSet exportSet) {
        switch (exportSet) {
            case WORKS:
                return "work";
            case EDUCATION:
                return "education";
            case EMPLOYMENTS:
                return "employment";
        }

        return ""; // Should never happen
    }

    @Nullable
    private Class<?> getConverterClass(ExportSet exportSet) {
        switch (exportSet) {
            case WORKS:
                return WorkConverter.class;
            case EDUCATION:
                return EducationConverter.class;
            case EMPLOYMENTS:
                return EmploymentConverter.class;
        }

        return null;
    }

    @Nullable
    private Method getConversionMethod(ExportSet exportSet) {
        Class<?> converterClass = getConverterClass(exportSet);

        if (converterClass == null) {
            return null; // should never happen
        }

        try {
            return converterClass.getMethod("toOrcidModel", Map.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isRecordValid(Object record) {
        if (record instanceof InvolvementDTO) {
            Organization org = ((InvolvementDTO) record).getOrganization();
            if (org == null) {
                return false;
            }

            Address adr = org.getAddress();
            if (adr == null) {
                return false;
            }

            if (!valueExists(adr.getCity()) || !valueExists(adr.getCountry())) {
                return false;
            }

            return org.getDisambiguatedOrganization() != null;
        } else if (record instanceof WorkDTO) {
            return ((WorkDTO) record).getExternalIds() != null &&
                ((WorkDTO) record).getExternalIds().getExternalId() != null &&
                !((WorkDTO) record).getExternalIds().getExternalId().isEmpty();
        }

        return true;
    }

    private boolean valueExists(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
