package edu.cornell.mannlib.vivo.orcid.export.model.work;

import java.util.List;

public class Contributors {

    private List<Contributor> contributor;


    public Contributors() {
    }

    public Contributors(List<Contributor> contributor) {
        this.contributor = contributor;
    }

    public List<Contributor> getContributor() {
        return contributor;
    }

    public void setContributor(List<Contributor> contributor) {
        this.contributor = contributor;
    }
}
