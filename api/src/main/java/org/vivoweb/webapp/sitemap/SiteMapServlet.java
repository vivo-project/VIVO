/* $This file is distributed under the terms of the license in LICENSE$ */

package org.vivoweb.webapp.sitemap;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "SiteMapServlet", urlPatterns = {"/robots.txt","/sitemap.xml"})
public class SiteMapServlet extends VitroHttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI  = request.getRequestURI();

        if (requestURI != null) {
            if (requestURI.contains("robots.txt")) {
                String robotsPath = getServletContext().getRealPath("/robots.txt");
                String contextPath = request.getContextPath();

                StringBuilder builder = new StringBuilder("Sitemap: ");
                builder.append(getSchemeAndServer(request));

                if (!"/".equals(contextPath)) {
                    builder.append(contextPath);
                }
                builder.append("/sitemap.xml");

                response.getOutputStream().println(builder.toString());
                response.getOutputStream().println();

                InputStream is = null;
                try {
                    is = new FileInputStream(robotsPath);
                    IOUtils.copy(is, response.getOutputStream());
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            } else {
                final String schemeAndServer = getSchemeAndServer(request);

                String query = QueryConstants.getSparqlPrefixQuery() +
                        "SELECT ?person\n" +
                        "WHERE\n" +
                        "{\n" +
                        "  ?person a foaf:Person .\n" +
                        "}\n";

                final VitroRequest vreq = new VitroRequest(request);
                final ServletOutputStream out = response.getOutputStream();

                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

                try {
                    vreq.getRDFService().sparqlSelectQuery(query, new ResultSetConsumer() {
                        @Override
                        protected void processQuerySolution(QuerySolution qs) {
                            String person = qs.getResource("person").getURI();

                            String profileUrl = UrlBuilder.getIndividualProfileUrl(person, vreq);

                            if (!StringUtils.isEmpty(profileUrl)) {
                                try {
                                    out.println("  <url>");
                                    out.println("    <loc>" + schemeAndServer + profileUrl + "</loc>");
                                    out.println("    <changefreq>weekly</changefreq>");
                                    out.println("  </url>");
                                } catch (Exception e) {
                                }
                            }
                        }
                    });
                } catch (RDFServiceException rse) {
                }

                out.println("</urlset>");
            }
        }
        super.doGet(request, response);
    }

    private String getSchemeAndServer(HttpServletRequest request) {
        String serverName  = request.getServerName();
        int serverPort     = request.getServerPort();
        String scheme      = request.getScheme();

        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            StringBuilder builder = new StringBuilder();
            builder.append(scheme).append("://").append(serverName);
            if (("http".equalsIgnoreCase(scheme) && serverPort != 80) || ("https".equalsIgnoreCase(scheme) && serverPort != 443) ) {
                builder.append(":").append(serverPort);
            }

            return builder.toString();
        }

        return "";
    }
}
