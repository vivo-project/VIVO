package edu.cornell.mannlib.vivo.harvest.configmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportModule {

    private final Map<String, ScheduledTaskMetadata> scheduledTasks = new HashMap<>();

    private final List<String> logFiles = new ArrayList<>();

    private String name;

    private Map<String, String> description;

    private String resolvedDescription;

    private String path;

    private boolean running = false;

    private List<ExportParameter> parameters;

    private boolean schedulable = false;

    private boolean manualRunLogExists = false;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public String getResolvedDescription() {
        return resolvedDescription;
    }

    public void setResolvedDescription(String resolvedDescription) {
        this.resolvedDescription = resolvedDescription;
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

    public boolean isSchedulable() {
        return schedulable;
    }

    public void setSchedulable(boolean schedulable) {
        this.schedulable = schedulable;
    }

    public Map<String, ScheduledTaskMetadata> getScheduledTasks() {
        return scheduledTasks;
    }

    public List<String> getLogFiles() {
        return logFiles;
    }

    public boolean isManualRunLogExists() {
        return manualRunLogExists;
    }

    public void setManualRunLogExists(boolean manualRunLogExists) {
        this.manualRunLogExists = manualRunLogExists;
    }
}
