package hdfs;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NameNodeDaemon extends Remote{
    /**
     * Renvoi le nombre de fragment composant ce fichier
     * @param fname : String, le nom du fichier (lors de l'enregistrement dans HDFS).
     * @return int, le nombre de fragment constituant le fichier.
     * @throws RemoteException
     */
    int getNumberOfChunk(String fname) throws RemoteException;

    /**
     * Localise un fragment spécifique d'un fichier
     * @param fname : String, le nom du fichier (lors de l'enregistrement dans HDFS).
     * @param chunkId : int, le numero du fragment.
     * @return les meta données du fragment
     * @throws RemoteException
     */
    ChunkMetaData locateChunk(String fname, int chunkId) throws RemoteException;

    /**
     * Enregistre un fichier dans le NameNode.
     * @param reduceFactor nombre de reduce.
     * @param fileIN les MetaDonnées du fichier.
     * @throws RemoteException
     */
    void registerFile(FileMetaData fileIN) throws RemoteException;

    /**
     * Retire un fichier du NameNode
     * @param fileName : String, le nom du fichier (lors de l'enregistrement dans HDFS).
     * @throws RemoteException
     */
    boolean removeFile(String fileName) throws RemoteException;

    /**
     * Localise un fichier.
     * @param fname : String, le nom du fichier (lors de l'enregistrement dans HDFS).
     * @return un FileMetaData contenant toutes les données concernant le fichier.
     * @throws RemoteException
     */
    FileMetaData locateFile(String fname) throws RemoteException;

    /**
     * Liste tous les fichier enregistré sur HDFS.
     * @return La liste des FileMetaData de chaque fichier.
     * @throws RemoteException
     */
    FileMetaData[] getFiles() throws RemoteException;


}
