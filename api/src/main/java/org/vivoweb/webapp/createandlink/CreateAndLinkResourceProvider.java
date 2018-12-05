package org.vivoweb.webapp.createandlink;

public interface CreateAndLinkResourceProvider {
    String normalize(String id);

    String getLabel();

    ExternalIdentifiers allExternalIDsForFind(String externalId);

    String findInExternal(String id, Citation citation);

    ResourceModel makeResourceModel(String externalId, String externalResource);
}
