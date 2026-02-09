package edu.cornell.mannlib.vivo.harvest;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportConfig;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HarvestContextSetup implements ServletContextListener {

    private static final Log log = LogFactory.getLog(HarvestContextSetup.class);


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        loadModules(ctx);
    }

    private void loadModules(ServletContext ctx) {
        String harvesterDirectory = ConfigurationProperties.getInstance().getProperty("harvester.directory");
        String harvesterConfigurationPath =
            ConfigurationProperties.getInstance().getProperty("harvester.configuration");
        Path fullPath = Paths.get(harvesterDirectory, harvesterConfigurationPath).normalize();

        try (InputStream is = Files.newInputStream(fullPath)) {
            ObjectMapper mapper = new ObjectMapper();
            ExportConfig config = mapper.readValue(is, ExportConfig.class);

            HarvestContext.modules = config.getExportModules();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load export modules config", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        HarvestContext.modules.clear();
    }
}