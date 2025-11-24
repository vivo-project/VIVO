package edu.cornell.mannlib.vivo.orcid.export.model.work;

public class ContributorOrcid {

    private String uri;

    private String path;

    private String host;


    public ContributorOrcid() {
    }

    public ContributorOrcid(String uri, String path, String host) {
        this.uri = uri;
        this.path = path;
        this.host = host;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
