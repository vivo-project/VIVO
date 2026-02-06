package edu.cornell.mannlib.vivo.harvest;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/harvest-status")
public class HarvestStatusController extends HttpServlet {

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
        throws IOException {

        String module = req.getParameter("module");

        boolean running =
            HarvestJobRegistry.isRunning(module);

        resp.setContentType("application/json");
        resp.getWriter()
            .write("{\"running\":" + running + "}");
    }
}
