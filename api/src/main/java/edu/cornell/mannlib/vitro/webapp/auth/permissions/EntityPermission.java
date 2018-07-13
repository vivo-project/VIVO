/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.auth.permissions;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractDataPropertyStatementAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractObjectPropertyStatementAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.FauxProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vitro.webapp.utils.RelationshipCheckerRegistry;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.Lock;
import org.apache.jena.vocabulary.RDF;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class EntityPermission extends Permission {
    protected EntityPermission(String uri) {
        super(uri);
    }

    /**
     * Static fields for all EntityPermissions
     */
    private static final Map<String, EntityPermission> allInstances = new HashMap<String, EntityPermission>();
    private static ContextModelAccess ctxModels = null;

    /**
     * Instance fields for each EntityPermission
     */
    private final Set<PropertyDao.FullPropertyKey> authorizedKeys = new HashSet<>();
    private final Set<String> authorizedResources = new HashSet<>();
    private boolean limitToRelatedUser = false;

    public static List<EntityPermission> getAllInstances(ContextModelAccess models) {
        if (ctxModels == null && models != null) {
            ctxModels = models;
        }

        if (allInstances.isEmpty()) {
            if (ctxModels == null) {
                throw new IllegalStateException("ContextModelAccess must be initialized");
            }

            getAllInstances(EntityDisplayPermission.class);
            getAllInstances(EntityUpdatePermission.class);
            getAllInstances(EntityPublishPermission.class);

            updateAllPermissions();
        }

        return new ArrayList<EntityPermission>(allInstances.values());
    }

    private static void getAllInstances(Class clazz) {
        if (!EntityPermission.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Must supply a class derived from EntityPermission");
        }

        OntModel accountsModel = ctxModels.getOntModel(ModelNames.USER_ACCOUNTS);
        try {
            accountsModel.enterCriticalSection(Lock.READ);

            StmtIterator typeIter = accountsModel.listStatements(null, RDF.type, accountsModel.getResource("java:" + clazz.getName() + "#Set"));
            while (typeIter.hasNext()) {
                Statement stmt = typeIter.next();
                if (stmt.getSubject().isURIResource()) {
                    String uri = stmt.getSubject().getURI();

                    Constructor<?> ctor = null;
                    try {
                        ctor = clazz.getConstructor(String.class);
                        EntityPermission permission = (EntityPermission) ctor.newInstance(new Object[]{uri});

                        StmtIterator limitIter = accountsModel.listStatements(stmt.getSubject(), RDF.type, accountsModel.getResource("java:" + clazz.getName() + "#SetLimitToRelatedUser"));
                        if (limitIter.hasNext()) {
                            permission.limitToRelatedUser = true;
                        }

                        allInstances.put(uri, permission);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            accountsModel.leaveCriticalSection();
        }
    }

    public static void updateAllPermissions() {
        if (allInstances.isEmpty()) {
            return;
        }

        WebappDaoFactory wadf = ctxModels.getWebappDaoFactory();

        Map<String, PropertyDao.FullPropertyKey> propertyKeyMap = new HashMap<>();

        for (ObjectProperty oProp : wadf.getObjectPropertyDao().getAllObjectProperties()) {
            propertyKeyMap.put(oProp.getURI(), new PropertyDao.FullPropertyKey(oProp.getURI()));
            for (FauxProperty fProp : wadf.getFauxPropertyDao().getFauxPropertiesForBaseUri(oProp.getURI())) {
                propertyKeyMap.put(fProp.getConfigUri(), new PropertyDao.FullPropertyKey(fProp.getDomainURI(), fProp.getBaseURI(), fProp.getRangeURI()));
            }
        }
        for (DataProperty dProp : wadf.getDataPropertyDao().getAllDataProperties()) {
            propertyKeyMap.put(dProp.getURI(), new PropertyDao.FullPropertyKey(dProp.getURI()));
        }

        for (EntityPermission instance : allInstances.values()) {
            instance.update(propertyKeyMap);
        }
    }

    public static void updateAllPermissionsFor(Property p) {
        if (allInstances.isEmpty()) {
            return;
        }

        for (EntityPermission instance : allInstances.values()) {
            instance.updateFor(p);
        }
    }

    protected void update(Map<String, PropertyDao.FullPropertyKey> propertyKeyMap) {
        List<PropertyDao.FullPropertyKey> newKeys = new ArrayList<>();
        List<String> newResources = new ArrayList<>();

        OntModel accountsModel = ctxModels.getOntModel(ModelNames.USER_ACCOUNTS);
        accountsModel.enterCriticalSection(Lock.READ);
        StmtIterator propIter = null;
        try {
            propIter = accountsModel.listStatements(accountsModel.getResource(this.uri), accountsModel.getProperty(VitroVocabulary.PERMISSION_FOR_ENTITY), (RDFNode) null);
            while (propIter.hasNext()) {
                Statement proptStmt = propIter.next();
                if (proptStmt.getObject().isURIResource()) {
                    String uri = proptStmt.getObject().asResource().getURI();
                    PropertyDao.FullPropertyKey key = propertyKeyMap.get(uri);
                    if (key != null) {
                        newKeys.add(key);
                    } else {
                        newResources.add(uri);
                    }
                }
            }
        } finally {
            if (propIter != null) {
                propIter.close();
            }
            accountsModel.leaveCriticalSection();
        }

        // replace authorized keys
        synchronized (authorizedKeys) {
            authorizedKeys.clear();
            authorizedKeys.addAll(newKeys);
        }

        // replace authorized resources
        synchronized (authorizedResources) {
            authorizedResources.clear();
            authorizedResources.addAll(newResources);
        }
    }

    public void updateFor(Property p) {
        String uri = null;
        PropertyDao.FullPropertyKey key = null;

        if (p instanceof FauxProperty) {
            FauxProperty fp = (FauxProperty)p;
            uri = fp.getConfigUri();
            key = new PropertyDao.FullPropertyKey(fp.getDomainURI(), fp.getBaseURI(), fp.getRangeURI());
        } else {
            uri = p.getURI();
            key = new PropertyDao.FullPropertyKey(p.getURI());
        }

        OntModel accountsModel = ctxModels.getOntModel(ModelNames.USER_ACCOUNTS);
        accountsModel.enterCriticalSection(Lock.READ);

        StmtIterator propIter = null;
        try {
            propIter = accountsModel.listStatements(accountsModel.getResource(this.uri), accountsModel.getProperty(VitroVocabulary.PERMISSION_FOR_ENTITY), accountsModel.getResource(uri));
            if (propIter.hasNext()) {
                synchronized (authorizedKeys) {
                    authorizedKeys.add(key);
                }

                synchronized (authorizedResources) {
                    authorizedResources.add(uri);
                }
            } else {
                synchronized (authorizedKeys) {
                    authorizedKeys.remove(key);
                }

                synchronized (authorizedResources) {
                    authorizedResources.remove(uri);
                }
            }
        } finally {
            if (propIter != null) {
                propIter.close();
            }
            accountsModel.leaveCriticalSection();
        }
    }

    protected boolean isAuthorizedFor(AbstractPropertyStatementAction action, List<String> userUris) {
        // If we are not limiting to only objects that the user has a relationship with
        // We can just authorise the access right now
        if (!limitToRelatedUser) {
            return true;
        }

        // Obtain the subject and object URIs
        String subjectUri = null;
        String objectUri = null;

        if (action instanceof AbstractDataPropertyStatementAction) {
            subjectUri = ((AbstractDataPropertyStatementAction)action).getSubjectUri();
        } else if (action instanceof AbstractObjectPropertyStatementAction) {
            subjectUri = ((AbstractObjectPropertyStatementAction)action).getSubjectUri();
            objectUri = ((AbstractObjectPropertyStatementAction)action).getObjectUri();
        }

        // If the subject or object is a user URI for the current user, authorise access
        for (String userUri : userUris) {
            if (subjectUri != null && userUri.equals(subjectUri)) {
                return true;
            }

            if (objectUri != null && userUri.equals(subjectUri)) {
                return true;
            }
        }

        return RelationshipCheckerRegistry.anyRelated(action.getOntModel(), Arrays.asList(action.getResourceUris()), userUris);
    }

    protected boolean isAuthorizedFor(Property prop) {
        if (RequestedAction.SOME_URI.equals(prop.getURI())) {
            return true;
        }

        synchronized (authorizedKeys) {
            if (authorizedKeys.contains(new PropertyDao.FullPropertyKey(prop))) {
                return true;
            }

            return authorizedKeys.contains(new PropertyDao.FullPropertyKey(prop.getURI()));
        }
    }
}
