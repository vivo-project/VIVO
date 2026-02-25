package edu.cornell.mannlib.vivo.harvest.controller;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vivo.harvest.HarvestJobRegistry;
import edu.cornell.mannlib.vivo.harvest.InternalScheduleOperations;
import edu.cornell.mannlib.vivo.harvest.RoleCheckUtility;

@WebServlet("/stopWorkflow")
public class WorkflowStopController extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserAccount acc = LoginStatusBean.getCurrentUser(req);
        if (acc == null || !RoleCheckUtility.isAdmin(acc)) {
            resp.sendError(401);
            return;
        }

        String module = req.getParameter("module");

        HarvestJobRegistry.stopJob(InternalScheduleOperations.sanitizeModuleName(module));

        resp.setContentType("application/json");
        resp.getWriter().write("{\"stopped\":true}");
    }
}
