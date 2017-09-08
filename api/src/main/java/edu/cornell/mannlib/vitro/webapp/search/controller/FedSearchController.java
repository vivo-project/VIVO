package edu.cornell.mannlib.vitro.webapp.search.controller;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.ApplicationDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(name = "FedSearchController", urlPatterns = {"/FS.xml","/ctsasearch"} ) // fedsearch
public class FedSearchController extends PagedSearchController {
    private static final Log log = LogFactory.getLog(FedSearchController.class);

    private String classgroup;
    private String populationType;

    private String getClassgroup(HttpServletRequest req) {
        if (classgroup == null) {
            ConfigurationProperties configuration = ConfigurationProperties.getBean(req.getSession().getServletContext());
            classgroup = configuration.getProperty("ctsa.classgroup", "http://vivoweb.org/ontology#vitroClassGrouppeople");
        }

        return classgroup;
    }

    private String getPopulationType(HttpServletRequest req) {
        if (populationType == null) {
            ConfigurationProperties configuration = ConfigurationProperties.getBean(req.getSession().getServletContext());
            populationType = configuration.getProperty("ctsa.classgroup.type", "faculty,staff,students");
        }

        return populationType;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        String serverBase = url.substring(0, url.indexOf(uri));

        if (request.getRequestURI().contains("FS.xml")) {
            try {
                VitroRequest vreq = new VitroRequest(request);
                Map<String, Object> body = new HashMap<String, Object>();
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/xml;charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=FS.xml");

                ApplicationDao aDao = vreq.getUnfilteredWebappDaoFactory().getApplicationDao();
                ApplicationBean applicationForEditing = aDao.getApplicationBean();

                body.put("ApplicationName", applicationForEditing.getApplicationName());
                body.put("serverBase", serverBase);

                writeTemplate("search-fs.ftl", body, request, response);
            } catch (Exception e) {
                log.error(e, e);
            }
        } else {
            try {
                Map<String, String[]> additionalParams = new TreeMap<>();

                String classgroup = getClassgroup(request);

                additionalParams.put("classgroup", new String[] { classgroup });

                VitroRequest vreq = new VitroRequest(new RequestWrapper(request, additionalParams));
                ResponseValues rvalues = processRequest(vreq);

                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/xml;charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=search.xml");
                Map<String, Object> body = new HashMap<String, Object>();

                body.putAll(rvalues.getMap());
                // Note - the template requires the following properties from the above map
                // querytext
                // hitCount

                body.put("populationType", getPopulationType(request));
                body.put("classgroup", classgroup);
                body.put("serverBase", serverBase);
                writeTemplate("search-fsresult.ftl", body, request, response);
            } catch (Exception e) {
                log.error(e, e);
            }
        }
    }

    private class RequestWrapper extends HttpServletRequestWrapper {
        private Map<String, String[]> allParameters = null;

        public RequestWrapper(HttpServletRequest request, final Map<String, String[]> additionalParams) {
            super(request);
            allParameters = new TreeMap<String, String[]>();
            allParameters.putAll(super.getParameterMap());
            allParameters.putAll(additionalParams);
            allParameters = Collections.unmodifiableMap(allParameters);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return allParameters;
        }

        @Override
        public String getParameter(final String name) {
            String[] strings = getParameterMap().get(name);
            if (strings != null) {
                return strings[0];
            }
            return super.getParameter(name);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(getParameterMap().keySet());
        }

        @Override
        public String[] getParameterValues(final String name) {
            return getParameterMap().get(name);
        }
    }
}
