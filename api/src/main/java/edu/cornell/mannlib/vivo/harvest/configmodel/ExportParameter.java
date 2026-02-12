package edu.cornell.mannlib.vivo.harvest.configmodel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExportParameter {

    private String name;

    private String type;

    private String symbol;

    @JsonProperty("default")
    private String defaultValue;

    private String acceptType;

    private String group;

    private String tmpLocation;

    // separator between multiple subfields in a group
    private String groupSep;

    // separator inside group key:value pair
    private String kvSep;

    private boolean required;

    private List<ExportParameter> subfields;

    private List<String> options;

    private boolean startDateAttribute;

    private boolean endDateAttribute;


    public String getGroupSep() {
        return groupSep;
    }

    public void setGroupSep(String groupSep) {
        this.groupSep = groupSep;
    }

    public String getKvSep() {
        return kvSep;
    }

    public void setKvSep(String kvSep) {
        this.kvSep = kvSep;
    }

    public String getTmpLocation() {
        return tmpLocation;
    }

    public void setTmpLocation(String tmpLocation) {
        this.tmpLocation = tmpLocation;
    }

    public String getAcceptType() {
        return acceptType;
    }

    public void setAcceptType(String acceptType) {
        this.acceptType = acceptType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<ExportParameter> getSubfields() {
        return subfields;
    }

    public void setSubfields(List<ExportParameter> subfields) {
        this.subfields = subfields;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public boolean isStartDateAttribute() {
        return startDateAttribute;
    }

    public void setStartDateAttribute(boolean startDateAttribute) {
        this.startDateAttribute = startDateAttribute;
    }

    public boolean isEndDateAttribute() {
        return endDateAttribute;
    }

    public void setEndDateAttribute(boolean endDateAttribute) {
        this.endDateAttribute = endDateAttribute;
    }
}

