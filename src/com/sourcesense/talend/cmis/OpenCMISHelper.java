package com.sourcesense.talend.cmis;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import java.util.HashMap;
import java.util.Map;

public class OpenCMISHelper {
    private final String url;
    private final String repositoryId;
    private final String userName;
    private final String password;
    private Session session;

    public OpenCMISHelper(String url, String repositoryId, String userName, String password) {
        this.url = url;
        this.repositoryId = repositoryId;
        this.userName = userName;
        this.password = password;
        initSession();
    }

    private void initSession() {
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(SessionParameter.USER, userName);
        parameter.put(SessionParameter.PASSWORD, password);
        parameter.put(SessionParameter.ATOMPUB_URL, url);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        if (repositoryId != null && !repositoryId.isEmpty()) {
            parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);
            this.session = SessionFactoryImpl.newInstance().createSession(parameter);
        } else {
            this.session = SessionFactoryImpl.newInstance().getRepositories(parameter).get(0).createSession();
        }
    }

    public ItemIterable<QueryResult> query(String query) {
        OperationContext operationContext = new OperationContextImpl();
        operationContext.setMaxItemsPerPage(10);
        return this.session.query(query, false, operationContext);
    }

    public Document getDocument(QueryResult queryResult) {
        String objectId = (String) queryResult.getPropertyById("cmis:objectId").getFirstValue();
        ObjectIdImpl objectIdImpl = new ObjectIdImpl(objectId);
        return (org.apache.chemistry.opencmis.client.api.Document) this.session.getObject(objectIdImpl);
    }
}
