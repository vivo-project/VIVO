package edu.cornell.mannlib.vivo.orcid.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vivo.orcid.controller.OrcidTokenExchange;
import edu.cornell.mannlib.vivo.orcid.util.OrcidIdOperationsUtil;
import edu.cornell.mannlib.vivo.orcid.util.Scheduled;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class OrcidSyncService {

    private final String clientId;

    private final String clientPassword;

    private final String environment;


    public OrcidSyncService(String clientId, String clientPassword, String environment) {
        this.clientId = clientId;
        this.clientPassword = clientPassword;
        this.environment = environment;
    }

    @Scheduled(cron = "${orcid.sync.cron}")
    public void syncOrcidProfiles() {
        getIndividualsWithBoundTokens(OrcidIdOperationsUtil.ACCESS_TOKEN_PROPERTY)
            .forEach(
                (individual, accessToken) ->
                    System.out.println(
                        "SYNCING: " + individual + " WITH " + OrcidIdOperationsUtil.decryptSecret(accessToken)));
    }

    @Scheduled(cron = "${orcid.refresh.cron}")
    public void refreshTokens() {
        getIndividualsWithBoundTokens(OrcidIdOperationsUtil.REFRESH_TOKEN_PROPERTY)
            .forEach(this::refreshSingleToken);
    }

    private void refreshSingleToken(String individualUri, String refreshTokenValue) {
        OrcidTokenExchange tokenExchanger = new OrcidTokenExchange(
            clientId,
            clientPassword,
            environment.equals("sandbox")
        );

        try {
            OrcidTokenExchange.OrcidTokenResponse tokenResponse =
                tokenExchanger.refreshToken(OrcidIdOperationsUtil.decryptSecret(refreshTokenValue));

            OrcidIdOperationsUtil.updateOrcidCredentialsForUser(
                individualUri,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                tokenResponse.getExpiresIn()
            );
        } catch (IOException e) {
            System.out.println("Refreshing of ORCID access token failed, reason: " + e.getMessage());
        }
    }

    @Scheduled(cron = "${orcid.cleanup.cron}")
    public void cleanupExpiredTokens() {
        getIndividualsWithBoundTokens(OrcidIdOperationsUtil.ACCESS_TOKEN_PROPERTY)
            .forEach(
                (individual, accessToken) -> getIndividualsWithExpirationInformation().forEach(
                    (individualUri, tokenInformation) -> {
                        if (OrcidIdOperationsUtil.shouldCleanup(Long.parseLong(tokenInformation[0]),
                            Integer.parseInt(tokenInformation[1]))) {
                            OrcidIdOperationsUtil.removeOrcidCredentialsForUser(individualUri);
                        }
                    }));
    }

    private Map<String, String> getIndividualsWithBoundTokens(String tokenProperty) {
        OntModel displayModel = getOntModel();

        StmtIterator iter =
            displayModel.listStatements(
                null,
                ResourceFactory.createProperty(OrcidIdOperationsUtil.ALLOW_PUSH_PROPERTY),
                ResourceFactory.createTypedLiteral(true)
            );

        Map<String, String> individualsWithAccessTokens = new HashMap<>();
        while (iter.hasNext()) {
            Statement s = iter.nextStatement();
            Resource individual = s.getSubject();

            if (individual != null) {
                StmtIterator accessTokenIter =
                    displayModel.listStatements(
                        individual,
                        ResourceFactory.createProperty(tokenProperty),
                        (RDFNode) null
                    );

                while (accessTokenIter.hasNext()) {
                    Statement accessTokenStatement = accessTokenIter.nextStatement();
                    if (accessTokenStatement.getSubject() != null) {
                        individualsWithAccessTokens.put(individual.getURI(),
                            accessTokenStatement.getLiteral().toString());
                    }
                }
            }
        }

        return individualsWithAccessTokens;
    }

    private Map<String, String[]> getIndividualsWithExpirationInformation() {
        OntModel displayModel = getOntModel();

        StmtIterator iter =
            displayModel.listStatements(
                null,
                ResourceFactory.createProperty(OrcidIdOperationsUtil.TOKEN_CREATED_AT_PROPERTY),
                (RDFNode) null
            );

        Map<String, String[]> individualsWithAccessTokens = new HashMap<>();
        while (iter.hasNext()) {
            Statement createdAtStatement = iter.nextStatement();
            Resource individual = createdAtStatement.getSubject();

            if (individual != null) {
                StmtIterator expiresInIter =
                    displayModel.listStatements(
                        individual,
                        ResourceFactory.createProperty(OrcidIdOperationsUtil.TOKEN_EXPIRES_IN_PROPERTY),
                        (RDFNode) null
                    );

                while (expiresInIter.hasNext()) {
                    Statement expiresInStatement = expiresInIter.nextStatement();
                    if (expiresInStatement.getSubject() != null) {
                        individualsWithAccessTokens.put(individual.getURI(),
                            new String[] {createdAtStatement.getLiteral().toString().split("\\^")[0],
                                expiresInStatement.getLiteral().toString().split("\\^")[0]});
                    }
                }
            }
        }

        return individualsWithAccessTokens;
    }

    private OntModel getOntModel() {
        ContextModelAccess cma = ModelAccess.getInstance();
        return cma.getOntModel(ModelNames.APPLICATION_METADATA);
    }
}
