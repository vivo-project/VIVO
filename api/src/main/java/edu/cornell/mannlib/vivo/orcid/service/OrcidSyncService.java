package edu.cornell.mannlib.vivo.orcid.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vivo.orcid.controller.OrcidTokenExchange;
import edu.cornell.mannlib.vivo.orcid.export.ExportSet;
import edu.cornell.mannlib.vivo.orcid.export.OrcidExportDataLoader;
import edu.cornell.mannlib.vivo.orcid.util.OrcidInternalOperationsUtil;
import edu.cornell.mannlib.vivo.orcid.util.Scheduled;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class OrcidSyncService {

    private static final Log log = LogFactory.getLog(OrcidSyncService.class);

    private final String clientId;

    private final String clientPassword;

    private final String environment;

    private final boolean sandboxed;

    private final OrcidExportDataLoader orcidExportDataLoader;


    public OrcidSyncService(String clientId, String clientPassword, String environment, RDFService rdfService) {
        this.clientId = clientId;
        this.clientPassword = clientPassword;
        this.environment = environment;
        this.sandboxed = environment.equals("sandbox");
        this.orcidExportDataLoader = new OrcidExportDataLoader(rdfService);
    }

    @Scheduled(cron = "${orcid.sync.cron}")
    public void syncOrcidProfiles() {
        getIndividualsWithBoundTokens(OrcidInternalOperationsUtil.ACCESS_TOKEN_PROPERTY)
            .forEach(
                (individual, accessToken) -> {
                    log.info(
                        "SYNCING: " + individual + " WITH " + OrcidInternalOperationsUtil.decryptSecret(accessToken));

                    String orcidId = OrcidInternalOperationsUtil.readOrcidIdForUser((individual));
                    if (orcidId == null) {
                        return; // should never happen
                    }

                    orcidExportDataLoader.exportSetForIndividual(individual, ExportSet.EDUCATION, orcidId, accessToken,
                        sandboxed);
                    orcidExportDataLoader.exportSetForIndividual(individual, ExportSet.EMPLOYMENTS, orcidId,
                        accessToken, sandboxed);
                    orcidExportDataLoader.exportSetForIndividual(individual, ExportSet.WORKS, orcidId, accessToken,
                        sandboxed);
                });
    }

    @Scheduled(cron = "${orcid.refresh.cron}")
    public void refreshTokens() {
        getIndividualsWithBoundTokens(OrcidInternalOperationsUtil.REFRESH_TOKEN_PROPERTY)
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
                tokenExchanger.refreshToken(OrcidInternalOperationsUtil.decryptSecret(refreshTokenValue));

            OrcidInternalOperationsUtil.updateOrcidCredentialsForUser(
                individualUri,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                tokenResponse.getExpiresIn()
            );
        } catch (IOException e) {
            log.warn("Refreshing of ORCID access token failed, reason: " + e.getMessage());
        }
    }

    @Scheduled(cron = "${orcid.cleanup.cron}")
    public void cleanupExpiredTokens() {
        getIndividualsWithBoundTokens(OrcidInternalOperationsUtil.ACCESS_TOKEN_PROPERTY)
            .forEach(
                (individual, accessToken) -> getIndividualsWithExpirationInformation().forEach(
                    (individualUri, tokenInformation) -> {
                        if (OrcidInternalOperationsUtil.shouldCleanup(Long.parseLong(tokenInformation[0]),
                            Integer.parseInt(tokenInformation[1]))) {
                            OrcidInternalOperationsUtil.removeOrcidCredentialsForUser(individualUri);
                        }
                    }));
    }

    private Map<String, String> getIndividualsWithBoundTokens(String tokenProperty) {
        OntModel displayModel = getOntModel();

        StmtIterator iter =
            displayModel.listStatements(
                null,
                ResourceFactory.createProperty(OrcidInternalOperationsUtil.ALLOW_PUSH_PROPERTY),
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
                ResourceFactory.createProperty(OrcidInternalOperationsUtil.TOKEN_CREATED_AT_PROPERTY),
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
                        ResourceFactory.createProperty(OrcidInternalOperationsUtil.TOKEN_EXPIRES_IN_PROPERTY),
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
        return cma.getOntModel(ModelNames.INTEGRATION_SETTINGS);
    }
}
