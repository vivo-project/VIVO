package edu.cornell.mannlib.vivo.harvest.configmodel;

import java.util.List;

public class ExportModule {

    private String name;

    private String description;

    private String path;

    private boolean running = false;

    private List<ExportParameter> parameters;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ExportParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ExportParameter> parameters) {
        this.parameters = parameters;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}

