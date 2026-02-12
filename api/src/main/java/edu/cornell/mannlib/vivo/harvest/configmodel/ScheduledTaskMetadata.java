package edu.cornell.mannlib.vivo.harvest.configmodel;

public class ScheduledTaskMetadata {

    private String taskName;

    private String taskUri;

    private String recurrenceType;

    private String nextRuntimeDate;


    public ScheduledTaskMetadata(String taskName, String taskUri, String recurrenceType, String nextRuntimeDate) {
        this.taskName = taskName;
        this.taskUri = taskUri;
        this.recurrenceType = recurrenceType;
        this.nextRuntimeDate = nextRuntimeDate;
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
}
