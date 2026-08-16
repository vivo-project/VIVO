package edu.cornell.mannlib.vivo.harvest.configmodel;

import java.util.List;

public class ExportConfig {

    private List<ExportModule> exportModules;


    public List<ExportModule> getExportModules() {
        return exportModules;
    }

    public void setExportModules(List<ExportModule> exportModules) {
        this.exportModules = exportModules;
    }
}

