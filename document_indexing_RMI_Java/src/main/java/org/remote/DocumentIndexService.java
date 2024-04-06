package org.remote;

import org.locals.DocumentEntity;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DocumentIndexService extends Remote {
    void addDocument(DocumentEntity document) throws RemoteException;
    List<DocumentEntity> searchDocuments(String searchText) throws RemoteException;
}

