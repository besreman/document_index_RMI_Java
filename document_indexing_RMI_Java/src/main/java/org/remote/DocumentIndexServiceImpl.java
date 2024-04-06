package org.remote;
import org.locals.DocumentEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DocumentIndexServiceImpl extends UnicastRemoteObject implements DocumentIndexService {

    public DocumentIndexServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void addDocument(DocumentEntity document) throws RemoteException {
        // Implement the logic to add the document to the database using JPA
        EntityManager entityManager = Persistence.createEntityManagerFactory("DocumentIndexPersistenceUnit").createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(document);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RemoteException("Failed to add document.", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<DocumentEntity> searchDocuments(String searchText) throws RemoteException {
        // Implement the logic to search documents in the database based on the searchText using JPA
        EntityManager entityManager = Persistence.createEntityManagerFactory("DocumentIndexPersistenceUnit").createEntityManager();
        List<DocumentEntity> searchResults;

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<DocumentEntity> criteriaQuery = criteriaBuilder.createQuery(DocumentEntity.class);
            Root<DocumentEntity> root = criteriaQuery.from(DocumentEntity.class);

            criteriaQuery.select(root)
                    .where(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));

            TypedQuery<DocumentEntity> query = entityManager.createQuery(criteriaQuery);
            searchResults = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Failed to search documents.", e);
        } finally {
            entityManager.close();
        }

        return searchResults;
    }
}
