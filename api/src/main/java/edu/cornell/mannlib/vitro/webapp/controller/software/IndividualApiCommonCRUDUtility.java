package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.application.ApplicationUtils;
import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.RDFServiceDataset;
import edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.GraphStoreFactory;

public class IndividualApiCommonCRUDUtility {

    public static void executeWithTransaction(HttpServletRequest req, HttpServletResponse resp,
                                              BiConsumer<GraphStore, String> action) throws IOException {
        VitroRequest vreq = new VitroRequest(req);
        SearchIndexer indexer = ApplicationUtils.instance().getSearchIndexer();
        Dataset ds = new RDFServiceDataset(vreq.getUnfilteredRDFService());
        GraphStore graphStore = GraphStoreFactory.create(ds);

        String entityUri;
        if (req.getMethod().equalsIgnoreCase("POST")) {
            entityUri = IndividualApiSparqlUtility.buildIndividualUri(UUID.randomUUID().toString());
        } else {
            String pathInfo = req.getPathInfo();
            if (Objects.isNull(pathInfo) || pathInfo.isEmpty()) {
                IndividualApiNetworkUtility.do400BadRequest("You have to provide a record identifier.", resp);
                return;
            }

            entityUri = IndividualApiSparqlUtility.buildIndividualUri(pathInfo.substring(1));
        }

        try {
            pauseIndexer(indexer);
            beginTransaction(ds);

            action.accept(graphStore, entityUri);

        } finally {
            commitTransaction(ds);
            unpauseIndexer(indexer);
        }
    }

    private static void pauseIndexer(SearchIndexer indexer) {
        if (indexer != null) {
            indexer.pause();
        }
    }

    private static void unpauseIndexer(SearchIndexer indexer) {
        if (indexer != null) {
            indexer.unpause();
        }
    }

    private static void beginTransaction(Dataset ds) {
        if (ds.supportsTransactions()) {
            ds.begin(ReadWrite.WRITE);
        }
    }

    private static void commitTransaction(Dataset ds) {
        if (ds.supportsTransactions()) {
            ds.commit();
            ds.end();
        }
    }

    public static void performDeleteOperation(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        String pathInfo = req.getPathInfo();
        if (Objects.isNull(pathInfo) || pathInfo.isEmpty()) {
            IndividualApiNetworkUtility.do400BadRequest("You have to provide a record identifier.", resp);
            return;
        }

        VitroRequest vreq = new VitroRequest(req);
        ApplicationBean appBean = vreq.getAppBean();
        String appName = appBean.getApplicationName().toLowerCase();
        URL url = new URL("http://" + req.getServerName() + ":" + req.getServerPort() + "/" + appName +
            "/deleteIndividualController?individualUri=" +
            URLEncoder.encode(IndividualApiSparqlUtility.buildIndividualUri(pathInfo.substring(1))) +
            "&redirectUrl=%2F");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        addCookiesToRequest(req, connection);

        connection.getResponseCode();

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        connection.disconnect();
    }

    private static void addCookiesToRequest(HttpServletRequest req, HttpURLConnection connection) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            StringBuilder cookieHeader = new StringBuilder();
            for (Cookie cookie : cookies) {
                cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
            }
            // Remove the trailing "; " at the end of the cookie string
            if (cookieHeader.length() > 0) {
                cookieHeader.setLength(cookieHeader.length() - 2);
            }
            connection.setRequestProperty("Cookie", cookieHeader.toString());
        }
    }
}
