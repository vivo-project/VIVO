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
import edu.cornell.mannlib.vivo.orcid.util.OrcidIdOperationsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrcidExportDataLoader {

    private static final Log log = LogFactory.getLog(OrcidExportDataLoader.class);

    private final RDFService rdfService;

    private final int BATCH_SIZE = 1;


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

    public void exportSetForIndividual(String individualUri, ExportSet exportSet, String orcidId) {
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

                    boolean alreadyPushed = OrcidIdOperationsUtil.wasResourcePushedInPast(resourceUri);

                    try {
                        Object result = conversionMethod.invoke(null, binding, orcidId);
                        System.out.println(result);
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
            return converterClass.getMethod("toOrcidModel", Map.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
