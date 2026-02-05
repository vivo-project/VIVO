package edu.cornell.mannlib.vivo.harvest.configmodel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExportParameter {

    private String name;

    private String type;

    private String symbol;

    @JsonProperty("default")
    private String defaultValue;

    private boolean required;

    private List<ExportParameter> subfields;


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
}

