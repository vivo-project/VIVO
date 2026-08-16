package edu.cornell.mannlib.vivo.harvest.configmodel;

import java.util.Map;

public class ScheduledTaskMetadata {

    private String taskName;

    private String taskUri;

    private String recurrenceType;

    private String nextRuntimeDate;

    private String moduleName;

    private Map<String, String> parameters;

    private String command;


    public ScheduledTaskMetadata(String taskName, String taskUri, String recurrenceType, String nextRuntimeDate,
                                 String moduleName, Map<String, String> parameters, String command) {
        this.taskName = taskName;
        this.taskUri = taskUri;
        this.recurrenceType = recurrenceType;
        this.nextRuntimeDate = nextRuntimeDate;
        this.moduleName = moduleName;
        this.parameters = parameters;
        this.command = command;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskUri() {
        return taskUri;
    }

    public void setTaskUri(String taskUri) {
        this.taskUri = taskUri;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public String getNextRuntimeDate() {
        return nextRuntimeDate;
    }

    public void setNextRuntimeDate(String nextRuntimeDate) {
        this.nextRuntimeDate = nextRuntimeDate;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
