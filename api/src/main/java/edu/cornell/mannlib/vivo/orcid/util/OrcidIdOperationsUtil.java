package edu.cornell.mannlib.vivo.orcid.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vivo.orcid.OrcidIdDataGetter;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;

public class OrcidIdOperationsUtil {

    public static String ACCESS_TOKEN_PROPERTY = "http://vivoweb.org/ontology/core#orcidAccessToken";

    public static String REFRESH_TOKEN_PROPERTY = "http://vivoweb.org/ontology/core#refreshToken";

    public static String TOKEN_EXPIRES_IN_PROPERTY = "http://vivoweb.org/ontology/core#tokenExpiresIn";

    public static String TOKEN_CREATED_AT_PROPERTY = "http://vivoweb.org/ontology/core#tokenCreatedAt";

    public static String PUSHED_TO_ORCID_REPOSITORY = "http://vivoweb.org/ontology/core#pushedToOrcidRepository";

    public static String ALLOW_PUSH_PROPERTY = VitroVocabulary.vitroURI + "allowedPush";

    private static final String ENCRYPTION_ALGORITHM = "AES";

    private static final String ENCRYPTION_TRANSFORMATION = "AES/ECB/PKCS5Padding";


    private static OntModel getOntModel(boolean isAboxAssertions) {
        ContextModelAccess cma = ModelAccess.getInstance();
        return cma.getOntModel(isAboxAssertions ? ModelNames.ABOX_ASSERTIONS : ModelNames.APPLICATION_METADATA);
    }

    public static void updateOrcidIdForUser(String individualUri, String orcidId) {
        OntModel ontologyModel = getOntModel(true);
        Resource person = ontologyModel.createResource(individualUri);
        Resource orcid = ontologyModel.createResource("http://orcid.org/" + orcidId);

        // person → orcidId → orcid
        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                person,
                ontologyModel.createProperty(OrcidIdDataGetter.ORCID_ID),
                orcid
            ));

        // orcid → confirmedOrcidId → person
        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                orcid,
                ontologyModel.createProperty(OrcidIdDataGetter.ORCID_IS_CONFIRMED),
                person
            ));
    }

    public static void updateOrcidCredentialsForUser(String individualUri, String accessToken, String refreshToken,
                                                     int expiresIn) {
        OntModel ontologyModel = getOntModel(false);
        Resource personResource = ResourceFactory.createResource(individualUri);

        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                personResource,
                ResourceFactory.createProperty(ACCESS_TOKEN_PROPERTY),
                ResourceFactory.createTypedLiteral(encryptSecret(accessToken))
            ));

        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                personResource,
                ResourceFactory.createProperty(REFRESH_TOKEN_PROPERTY),
                ResourceFactory.createTypedLiteral(encryptSecret(refreshToken))
            ));

        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                personResource,
                ResourceFactory.createProperty(TOKEN_EXPIRES_IN_PROPERTY),
                ResourceFactory.createTypedLiteral(expiresIn)
            ));

        long tokenCreationTime = Instant.now().getEpochSecond();
        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                personResource,
                ResourceFactory.createProperty(TOKEN_CREATED_AT_PROPERTY),
                ResourceFactory.createTypedLiteral(tokenCreationTime)
            ));
    }

    public static void removeOrcidCredentialsForUser(String individualUri) {
        OntModel ontologyModel = getOntModel(false);
        Resource personResource = ResourceFactory.createResource(individualUri);

        ontologyModel.removeAll(
            personResource,
            ResourceFactory.createProperty(ACCESS_TOKEN_PROPERTY),
            null
        );

        ontologyModel.removeAll(
            personResource,
            ResourceFactory.createProperty(REFRESH_TOKEN_PROPERTY),
            null
        );

        ontologyModel.removeAll(
            personResource,
            ResourceFactory.createProperty(TOKEN_EXPIRES_IN_PROPERTY),
            null
        );

        ontologyModel.removeAll(
            personResource,
            ResourceFactory.createProperty(TOKEN_CREATED_AT_PROPERTY),
            null
        );

        setAllowPushStatusForIndividual(individualUri, false);
    }

    public static void setAllowPushStatusForIndividual(String individualUri, boolean status) {
        OntModel ontologyModel = getOntModel(false);
        Resource personResource = ResourceFactory.createResource(individualUri);

        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                personResource,
                ResourceFactory.createProperty(ALLOW_PUSH_PROPERTY),
                ResourceFactory.createTypedLiteral(status)
            ));
    }

    public static boolean getAllowPushStatusForIndividual(String individualUri) {
        OntModel ontologyModel = getOntModel(false);
        Resource personResource = ResourceFactory.createResource(individualUri);

        StmtIterator iter = ontologyModel.listStatements(
            personResource,
            ResourceFactory.createProperty(ALLOW_PUSH_PROPERTY),
            (RDFNode) null
        );

        return iter.hasNext() && iter.nextStatement().getLiteral().getBoolean();
    }

    public static boolean hasConfiguredPushCredentials(String individualUri) {
        OntModel ontologyModel = getOntModel(false);
        Resource personResource = ResourceFactory.createResource(individualUri);

        StmtIterator iter = ontologyModel.listStatements(
            personResource,
            ResourceFactory.createProperty(ACCESS_TOKEN_PROPERTY),
            (RDFNode) null
        );

        return iter.hasNext();
    }

    /**
     * Checks if an item should be cleaned up based on its creation time and lifespan
     *
     * @param createdAt            Epoch seconds when the item was created
     * @param lastsForSeconds      How long the item should last in seconds
     * @param cleanupBufferSeconds Optional buffer seconds before actual expiration to trigger cleanup (default 0)
     * @return true if the item should be cleaned up, false otherwise
     */
    public static boolean shouldCleanup(long createdAt, int lastsForSeconds, Integer cleanupBufferSeconds) {
        if (createdAt <= 0) {
            return true; // Invalid creation time, clean it up
        }

        if (lastsForSeconds <= 0) {
            return true; // Zero or negative lifespan, clean it up immediately
        }

        int buffer = cleanupBufferSeconds != null ? cleanupBufferSeconds : 0;
        long expirationTime = createdAt + (lastsForSeconds - buffer);
        long currentTime = Instant.now().getEpochSecond();

        return currentTime > expirationTime;
    }

    /**
     * Overloaded method without buffer
     */
    public static boolean shouldCleanup(long createdAt, int lastsForSeconds) {
        return shouldCleanup(createdAt, lastsForSeconds, 0);
    }

    @Nullable
    public static String encryptSecret(String plainText) {
        ConfigurationProperties props = ConfigurationProperties.getInstance();
        String base64Key = props.getProperty("orcid.secrets.encryptionKey");

        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | BadPaddingException |
                 IllegalBlockSizeException | InvalidKeyException |
                 NoSuchPaddingException e) {
            return null;
        }
    }

    @Nullable
    public static String decryptSecret(String encryptedText) {
        ConfigurationProperties props = ConfigurationProperties.getInstance();
        String base64Key = props.getProperty("orcid.secrets.encryptionKey");

        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | BadPaddingException |
                 IllegalBlockSizeException | InvalidKeyException |
                 NoSuchPaddingException e) {
            return null;
        }
    }

    private static void saveModelStatement(OntModel ontologyModel, StatementImpl statement) {
        ontologyModel.removeAll(
            statement.getSubject(),
            statement.getPredicate(),
            null
        );

        ontologyModel.add(
            statement
        );
    }

    public static void markPushed(String resourceUri) {
        OntModel ontologyModel = getOntModel(false);

        saveModelStatement(ontologyModel, new StatementImpl(
            ResourceFactory.createResource(resourceUri),
            ResourceFactory.createProperty(PUSHED_TO_ORCID_REPOSITORY),
            ResourceFactory.createTypedLiteral(true)
        ));
    }

    public static boolean wasResourcePushedInPast(String resourceUri) {
        OntModel ontologyModel = getOntModel(false);

        StmtIterator iter = ontologyModel.listStatements(
            ResourceFactory.createResource(resourceUri),
            ResourceFactory.createProperty(PUSHED_TO_ORCID_REPOSITORY),
            (RDFNode) null
        );

        return iter.hasNext() && iter.nextStatement().getLiteral().getBoolean();
    }

    @Nullable
    public static String readOrcidIdForUser(String resourceUri) {
        OntModel ontologyModel = getOntModel(true);

        StmtIterator iter = ontologyModel.listStatements(
            ResourceFactory.createResource(resourceUri),
            ResourceFactory.createProperty(OrcidIdDataGetter.ORCID_ID),
            (RDFNode) null
        );

        if (iter.hasNext()) {
            Object orcidId = iter.nextStatement().getObject();
            if (orcidId != null) {
                return orcidId.toString();
            }
        }

        return null;
    }
}
