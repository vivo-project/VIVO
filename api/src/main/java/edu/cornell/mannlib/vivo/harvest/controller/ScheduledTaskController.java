package edu.cornell.mannlib.vivo.harvest.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vivo.harvest.InternalScheduleOperations;
import edu.cornell.mannlib.vivo.harvest.configmodel.ScheduledTaskMetadata;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;

@WebServlet("/scheduledWorkflow")
public class ScheduledTaskController extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        VitroRequest vreq = new VitroRequest(req);

        String moduleName = vreq.getParameter("module");
        String task = vreq.getParameter("taskToDelete");

        if (moduleName != null && task != null) {
            HarvestContext.modules.stream()
                .filter(module ->
                    module.getName().equals(moduleName))
                .findFirst()
                .ifPresent(module -> {

                    ScheduledTaskMetadata scheduledTask = module.getScheduledTasks().get(task);

                    if (scheduledTask != null) {
                        InternalScheduleOperations.removeScheduledTask(scheduledTask.getTaskUri());

                        module.getScheduledTasks()
                            .remove(task);
                    }
                });
        }

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
