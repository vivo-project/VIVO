package edu.cornell.mannlib.vivo.harvest.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vivo.harvest.InternalScheduleOperations;
import edu.cornell.mannlib.vivo.harvest.RoleCheckUtility;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;

@WebServlet("/workflowOutputLog")
public class WorkflowOutputLogController extends HttpServlet {

    private static final ConcurrentHashMap<String, Long> sinceDB = new ConcurrentHashMap<>();

    public static void resetLogPosition(String moduleName) {
        sinceDB.put(moduleName, 0L);
    }

    public static void cleanupLogPosition(String moduleName) {
        sinceDB.remove(moduleName);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserAccount acc = LoginStatusBean.getCurrentUser(req);
        if (acc == null || !RoleCheckUtility.isAdmin(acc)) {
            resp.sendError(401);
            return;
        }

        String moduleName = req.getParameter("module");
        File logFile = new File(
            HarvestContext.logFileLocation +
                "harvest-" + InternalScheduleOperations.sanitizeModuleName(moduleName) + ".log");

        StringBuilder newContent = new StringBuilder();
        long currentPosition = 0;

        if (logFile.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
                long fileLength = raf.length();
                long lastPosition = sinceDB.getOrDefault(moduleName, 0L);

                if (lastPosition > fileLength) {
                    lastPosition = 0;
                }

                raf.seek(lastPosition);

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = raf.read(buffer)) != -1) {
                    newContent.append(new String(buffer, 0, bytesRead));
                }

                currentPosition = raf.getFilePointer();
                sinceDB.put(moduleName, currentPosition);

            } catch (Exception e) {
                e.printStackTrace();
                sinceDB.put(moduleName, 0L);
            }
        }

        resp.setContentType("application/json");

        String escapedJson = escapeJson(newContent.toString());

        resp.getWriter().write(String.format(
            "{\"log\":\"%s\",\"position\":%d}",
            escapedJson,
            currentPosition
        ));
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
