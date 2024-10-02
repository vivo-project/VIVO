package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.cornell.mannlib.vitro.webapp.controller.api.sparqlquery.InvalidQueryTypeException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import org.apache.jena.query.QueryParseException;

public class IndividualApiNetworkUtility {

    public static void handleResponseContentType(HttpServletRequest req, HttpServletResponse resp) {
        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && !acceptHeader.isEmpty()) {
            resp.setContentType(acceptHeader);
        } else {
            resp.setContentType("application/json");
        }
    }

    public static String serializeToJSON(Object serializationObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper.writeValueAsString(serializationObject);
    }

    public static boolean isJsonRequest(HttpServletRequest req) {
        String acceptHeader = req.getHeader("Accept");
        return acceptHeader != null && acceptHeader.equals("application/json");
    }

    public static <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(req.getInputStream(), clazz);
    }

    public static void handleException(Exception e, String queryString, HttpServletResponse resp) throws IOException {
        if (e instanceof InvalidQueryTypeException) {
            do400BadRequest("Invalid query type: '" + queryString + "'", resp);
        } else if (e instanceof QueryParseException) {
            do400BadRequest("Failed to parse query: '" + queryString + "'", resp);
        } else if (e instanceof RDFServiceException) {
            do500InternalServerError("Problem executing the query.", e, resp);
        }
    }

    public static void do400BadRequest(String message, HttpServletResponse resp)
        throws IOException {
        resp.setStatus(400);
        resp.getWriter().println(message);
    }

    public static void do404NotFound(String message, HttpServletResponse resp)
        throws IOException {
        resp.setStatus(404);
        resp.getWriter().println(message);
    }

    public static void do500InternalServerError(String message, Exception e,
                                                HttpServletResponse resp) throws IOException {
        resp.setStatus(500);
        PrintWriter w = resp.getWriter();
        w.println(message);
        e.printStackTrace(w);
    }
}
