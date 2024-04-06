package org.locals;

import org.remote.DocumentIndexService;
import org.remote.DocumentIndexServiceImpl;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DocumentIndexRMIServer {
    public static void main(String[] args) {
        try {
            // Create an instance of the remote object
            DocumentIndexService documentIndexService = new DocumentIndexServiceImpl();

            // Start the RMI registry or obtain a reference to an existing one
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the remote object to a name in the RMI registry
            Naming.rebind("DocumentIndexService", documentIndexService);

            System.out.println("RMI server is running.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

