package hdfs;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataNameNodeDaemon extends Remote {
    // Enregistrer un démon auprès du DataNameNode
    void registerDaemon(NodeIdentifier newDaemon) throws RemoteException;

    NodeIdentifier[] getDaemons() throws RemoteException;

    boolean removeDaemon(NodeIdentifier removedDaemon) throws RemoteException;
}
