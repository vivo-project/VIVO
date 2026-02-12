package edu.cornell.mannlib.vivo.harvest.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vivo.harvest.RoleCheckUtility;

@WebServlet("/downloadWorkflowLog")
public class HarvestDownloadLogController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserAccount acc = LoginStatusBean.getCurrentUser(req);
        if (acc == null || !RoleCheckUtility.isAdmin(acc)) {
            resp.sendError(401);
            return;
        }

        String module = req.getParameter("module");
        if (module == null || module.trim().isEmpty()) {
            resp.sendError(400, "Module parameter is required");
            return;
        }

        File file = new File("/tmp/harvest-" + sanitizeModuleName(module) + ".log");

        if (!file.exists()) {
            resp.sendError(404);
            return;
        }

        resp.setContentType("text/plain");
        resp.setHeader(
            "Content-Disposition",
            "attachment; filename=\"" +
                file.getName() + "\"");

        Files.copy(file.toPath(), resp.getOutputStream());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserAccount acc = LoginStatusBean.getCurrentUser(req);
        if (acc == null || !RoleCheckUtility.isAdmin(acc)) {
            resp.sendError(401);
            return;
        }

        String module = req.getParameter("module");
        if (module == null || module.trim().isEmpty()) {
            resp.sendError(400, "Module parameter is required");
            return;
        }

        File file = new File("/tmp/harvest-" + sanitizeModuleName(module) + ".log");

        if (!file.exists()) {
            resp.sendError(404);
            return;
        }

        if (file.delete()) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.sendError(500, "Failed to delete file");
        }
    }

    private String sanitizeModuleName(String moduleName) {
        return moduleName.replaceAll("[^a-zA-Z0-9_-]", "");
    }
}
