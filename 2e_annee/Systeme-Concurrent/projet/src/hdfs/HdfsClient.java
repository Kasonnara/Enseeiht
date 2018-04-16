package hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.*;

import formats.*;
import exceptions.NameNodeUnreachable;

/**
 * Pour l'échange de message entre le client et le DataNode on envoie un objet de
 * type commande qui précise l'action souhaitée et le nom dufragement de fichier
 * à lire ou à ecrire ou à supprimer selon le cas
 * De méme Pour l'échange de message entre le client et le Na,eNode on envoie un objet de
 * type commandeDataNode qui précise l'action souhaitée et le nom de fichier
 * à lire ou à supprimer ou ..  selon le cas
 */

public class HdfsClient {

    private static int portNameNode;
    private static String IPNameNode;
    private static String pathToRead;
    private static Double chunkSize;
    private static boolean loadConfig = false;
    private static boolean verbose = false;
    private static String default_config_path = "./config/clients/localhost.properties";

    // load a properties file so the file ../config/localhost.properties should exist
    // ce fichier doit contenir adresse et le hdfsPort de NameNode
    // et le path a un dossier où on doit géner le fichier aprés la commande read
    public static void loadConfig(String config_path) {
        if (!loadConfig) {
            loadConfig = true;
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(config_path));
                // get the property value and print it out
                portNameNode = Integer.parseInt(prop.getProperty("portNameNode"));
                pathToRead = prop.getProperty("pathRead");
                IPNameNode = prop.getProperty("adresseNameNode");
                chunkSize = (prop.getProperty("chunkSize").equals("auto") ? null : Double.parseDouble(prop.getProperty("chunkSize")));
                verbose = Boolean.parseBoolean(prop.getProperty("verbose"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // permet d'afficher un message en cas d'erreur de saisie de la commande
    private static void usage() {
        System.out.println("Usage: java -classpath './bin' HdfsClient [-c config_path] read <file>");
        System.out.println("       java -classpath './bin' HdfsClient [-c config_path] write <line|kv> <file>");
        System.out.println("       java -classpath './bin' HdfsClient [-c config_path] delete <file>");
        System.out.println("       java -classpath './bin' HdfsClient [-c config_path] ls [file]");
    }

    /**
     * Pour la commande read on géner un fichier dont nom dérivé de le fichier dans
     * le hdfs par l’ajout du suffixe "-read" dont le dossier pathToRead qu'on le
     * récuperer depuis le fichier de configuration
     */
    public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>
        // appel des methodes depuis la ligne de commande
        long t1 = System.currentTimeMillis();
        if (args.length < 1) {
            usage();
            return;
        }
        // on utilise une liste pour pouvoir supprimer les argument optionnel une fois consommés
        List<String> argsList = Arrays.asList(args);

        loadConfig(parseOption(argsList, "-c", default_config_path));

        try {
            switch (argsList.get(0)) {
                case "ls":
                    if (argsList.size() < 2) {
                        HdfsLs(null);
                    } else {
                        HdfsLs(argsList.get(1));
                    }
                    break;
                case "read":
                    if (argsList.size() < 2) {
                        usage();
                        return;
                    }
                    HdfsRead(argsList.get(1), pathToRead + argsList.get(1) + "-read");
                    break;
                case "delete":
                    if (argsList.size() < 2) {
                        usage();
                        return;
                    }
                    HdfsDelete(args[1]);
                    break;
                case "write":
                    Format.Type fmt;
                    if (argsList.size() < 3) {
                        usage();
                        return;
                    }
                    if (argsList.get(1).equals("line"))
                        fmt = Format.Type.LINE;
                    else if (argsList.get(1).equals("kv"))
                        fmt = Format.Type.KV;
                    else {
                        usage();
                        return;
                    }
                    HdfsWrite(fmt, argsList.get(2), 2, 1);
                    break;
                default:
                    usage();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Temps d'execution " + (t2 - t1) + "ms.");
    }

    /**
     * permet de lister des informations sur les fichier stocké dans HDFS et sur le
     * chunk de chaque fichier . si name==null il liste tous les fichier de hdfs
     * sinon il affiche les métaDonnées de fichier dont le nom "name" et ses chunks
     *
     * il reçoit les metaDonnees de fichies envoye par le Name node et il l'affiche
     */
    public static void HdfsLs(String name){
        HdfsLs(name, default_config_path);
    }
    public static void HdfsLs(String name, String config_path) {
        loadConfig(default_config_path);


        FileMetaData[] liFiles = getAllFileMetadata();

        if (liFiles.length == 0) {
            if (name == null) {
                System.out.println("Hdfs vide");
            } else {
                System.out.println("ce fichier n'existe pas");
            }

        } else {
            affichageLs(liFiles);
        }

    }

    // permet d'afficher les metaDonnees de fichies et chunks
    private static void affichageLs(FileMetaData[] liFiles) {
        for (FileMetaData fm1 : liFiles) {
            System.out.println("- " + fm1.getfName() + "  taille: " + fm1.getTaille() + " bytes  format: "
                    + fm1.getFormat().toString());
            for (ChunkMetaData chM : fm1.getLiChunks()) {
                System.out.println("|--> " + chM.getChunkLocalFname() + "  Node: " + chM.getNodeIds()[0].getNodeIP() + ":"
                        + chM.getNodeIds()[0].getHdfsPort() + "  taille: " + chM.getTaille() + " bytes ");
            }
        }
    }

    /**
     * permet de supprimer les fragments d’un fichier stocké dans HDFS il reçoit les
     * identifients de chunks d'un fichier envoyé par le NameNode et il parcourt la
     * list de noeuds pour supprimer les fragments d’un fichier de nom hdfsFname
     */
    public static void HdfsDelete(String hdfsFname) {
        HdfsDelete(hdfsFname, default_config_path);
    }
    public static void HdfsDelete(String hdfsFname, String config_path) {
        loadConfig(config_path);
        // communication avec NameNode
        FileMetaData fmd= getFileMetadata(hdfsFname);

        if (fmd == null) {
            System.out.println("ce fichier n'existe pas");
            return;
        }

        // communication avec les DataNodes
        boolean FileDelted = true;

        for (ChunkMetaData chm : fmd.getLiChunks()) {
            for (NodeIdentifier ni : chm.getNodeIds()) {
                ConnexionBidirectionnel inoutDN = new ConnexionBidirectionnel(ni.getNodeIP(), ni.getHdfsPort());

                Commande cmd = new Commande(Commande.CommandeCode.CMD_DELETE, chm.getChunkLocalFname());
                try {
                    inoutDN.out.writeObject(cmd);
                    inoutDN.out.flush();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // pour la synchronisation
                try {
                    if (!(inoutDN.in.readBoolean())) {
                        FileDelted = false;
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                inoutDN.close();
            }
        }
        if (FileDelted && removeFile(hdfsFname)) {
            System.out.println("le fichier a été supprimé avec succes");
        } else {
            System.out.println("erreur lors de suppression de fichier");
        }

    }

    /**
     * permet d’écrire un fichier dans HDFS. il reçoit des identifiants de nodes
     * (ip,adresse) envoyé par le NameNode pour qu'il puisse le communiquer avec eux
     * Le fichier dont le nom est localFSSourceF est lu sur le système de fichiers
     * local, découpé en fragments selon le nombre de noeuds et les fragments sont
     * envoyés pour stockage sur les différentes noeuds. fmt est le format du
     * fichier (Format.Type.LINE ou Format.Type.KV). repF actor est le facteur de
     * duplication des fragments ; pour cette version il sera considéré comme valant
     * 1 (pas de duplication).
     *
     * aprés la fragmentation de fichier , il envoie les metaDonnées au NameNode
     * pour l'enregistrer
     */
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname) {
        HdfsWrite(fmt, localFSSourceFname, 1, 1);
    }
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, String config_path){
        HdfsWrite(fmt, localFSSourceFname, 1, -1, config_path);
    }
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor) {
        HdfsWrite(fmt, localFSSourceFname, repFactor, -1);
    }
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor, int reduceFactor){
        HdfsWrite(fmt,localFSSourceFname, repFactor, reduceFactor, default_config_path);
    }
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor, int reduceFactor, String config_path) {
        if (!loadConfig) loadConfig(config_path);

        File f = new File(localFSSourceFname);
        if (chunkSize == null) {

            if (!f.exists()) throw new RuntimeException("Ce fichier n'existe pas");

            NodeIdentifier[] liNodes = getsDaemons();
            chunkSize = (Math.ceil(f.length() / liNodes.length));
        }

        if (!f.exists()) throw new RuntimeException("Ce fichier n'existe pas");
        int nbChunks = (int)Math.ceil(f.length() / chunkSize);

        // communication avec NameNode
        NodeIdentifier[] liNodes = getsDaemons();

        Format lfreader = FormatTools.SelectFormat(fmt);
        lfreader.setFname(localFSSourceFname);
        lfreader.open(Format.OpenMode.R);

        String[] words = localFSSourceFname.split("/");
        localFSSourceFname = words[words.length - 1];

        // Init données
        ConnexionBidirectionnel[][] inouts = new ConnexionBidirectionnel[nbChunks][repFactor];
        int[] chunksSizes =  new int[nbChunks];
        for (int i = 0; i< nbChunks; i++){
            chunksSizes[i] = 0;
            for (int k = 0; k < repFactor; k++) {
                int index = (i + k) % liNodes.length;
                Commande cmd = new Commande(Commande.CommandeCode.CMD_WRITE, localFSSourceFname + i, fmt);
                try {
                    inouts[i][k] = new ConnexionBidirectionnel(liNodes[index].getNodeIP(), liNodes[index].getHdfsPort(), cmd);
                } catch (IOException e1) {
                    inouts[i] = null;
                }
            }
        }
        // Lire chaque KV et les envoyer aux différent DataNodes
        KV currentKv;
        //    System.out.println("i = "+i + ", condition = " + ((i + 1) * sizeChunk >= lfreader.getIndex()) + " index =" + lfreader.getIndex()+" size="+sizeChunk);
        int last_fileIndex = 0;
        while ((currentKv = lfreader.read()) != null) {
            int chunk_index = ((int)Math.floor(lfreader.getIndex() / chunkSize) % nbChunks);
            for (int k = 0; k < repFactor; k++) {
                if (inouts[chunk_index][k] != null) {
                    try {
                        inouts[chunk_index][k].out.writeObject(currentKv);
                        inouts[chunk_index][k].out.flush();
                    } catch (IOException e) {
                        // DataNode communication failure
                        inouts[chunk_index][k].close();
                        inouts[chunk_index][k] = null;
                        // TODO ptet quelque chose d'autre a faire
                    }
                }
            }
            chunksSizes[chunk_index]+= (int)lfreader.getIndex()-last_fileIndex;
        }

        // on envoie null pour informer le server qu'on a terminé l'envoie des messages
        for (int i = 0; i < nbChunks; i++) {
            for (int k = 0; k < repFactor; k++) {
                if (inouts[i][k] != null) {
                    try {
                        inouts[i][k].out.writeObject(null);
                        inouts[i][k].out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // pour la synchronisation
                    try {
                        inouts[i][k].in.readBoolean();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // fermer le socket
                    inouts[i][k].close();
                }
            }
        }

        // Générer le FileMetadata
        FileMetaData fileMD = new FileMetaData(localFSSourceFname, lfreader.getIndex(), fmt, repFactor,reduceFactor);

        // fermer le fichier de lecture
        lfreader.close();

        // Enregistrer les Chunk qui sont arrivés avec succès
        int count = 0;
        for (int i = 0; i < nbChunks; i++) {
            List<NodeIdentifier> chunkNodes = new LinkedList<>();
            for (int k = 0; k < repFactor; k++) {
                if (inouts[i][k] != null) {
                    chunkNodes.add(liNodes[(i + k) % liNodes.length]);
                }
            }
            fileMD.addChunk(new ChunkMetaData(chunkNodes.toArray(new NodeIdentifier[chunkNodes.size()]),
                    chunksSizes[i],
                    localFSSourceFname + i));
        }


        // Communication avec le NameNode
        registerFile(fileMD);
        System.out.println("Opération terminé avec succès");
    }

    /**
     * permet de lire un fichier de nom hdfsFname à partir de HDFS. on récupre les
     * identifients de chunks du NameNode pour qu'on puisse les récuprer Les
     * fragments du fichier sont lus à partir des différentes machines, concaténés
     * et stockés localement dans un fichier de nom localFSDestFname dans le dossier
     * "pathToRead" précisé dans la configuration
     */
    public static void HdfsRead(String hdfsFname, String localFSDestFname) {
        HdfsRead(hdfsFname, localFSDestFname, default_config_path);
    }
    public static void HdfsRead(String hdfsFname, String localFSDestFname, String config_path) {
        loadConfig(config_path);

        // Recupérer les MetaDonnées du fichier
        FileMetaData fmd = getFileMetadata(hdfsFname);
        if (fmd == null) throw new RuntimeException("HdfsRead : ce fichier n'existe pas " + hdfsFname);
        System.out.println("reading " + hdfsFname + " " + fmd.toString());


        // Initialiser l'écriture locale avec un Format
        Format lfWriter = FormatTools.SelectFormat(fmd.format);
        lfWriter.setFname(localFSDestFname);
        lfWriter.open(Format.OpenMode.W);

        // communication avec les DataNodes
        List<ChunkMetaData> chunks = (hdfsFname.equals(fmd.getfName()) ? fmd.getLiChunks() : fmd.getReduceChunks());
        for (ChunkMetaData chmd : chunks) {
            boolean succes = false;
            int l = 0;
            while (!succes && l < chmd.getNodeIds().length) {
                ConnexionBidirectionnel inoutDN = null;
                try {
                    System.out.println("Aquiring chunk " + chmd.getChunkLocalFname());
                    Commande cmd = new Commande(Commande.CommandeCode.CMD_READ, chmd.getChunkLocalFname()
                            , fmd.format);
                    inoutDN = new ConnexionBidirectionnel(chmd.getNodeIds()[l].getNodeIP(), chmd.getNodeIds()[l].getHdfsPort(), cmd);

                    KV kvReceived = null;
                    do {
                        try {
                            kvReceived = (KV) inoutDN.in.readObject();
                            if (kvReceived != null) {
                                lfWriter.write(kvReceived);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } while (kvReceived != null);

                    //

                    // pour la synchronisation
                    inoutDN.out.writeBoolean(true);
                    inoutDN.out.flush();
                    succes = true;
                } catch (IOException e) {
                    // fail to contact datanode
                    // succes reste a false donc on recommence sur le node suivant
                } finally {
                    if (inoutDN != null) {
                        // fermer le socket
                        inoutDN.close();
                    }
                }
                l += 1;
            }
        }
        // fermer le fichier
        lfWriter.close();
        System.out.println("Opération terminé avec succès");
    }


    /**
     * Commandes dec communication avec le NameNode
     */
    private static NodeIdentifier[] getsDaemons() {
        try {
            DataNameNodeDaemon nnd = (DataNameNodeDaemon) Naming.lookup("//" + IPNameNode + ":" + portNameNode + "/DataNameNodeDaemon");
            return nnd.getDaemons();
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            throw new NameNodeUnreachable("Liste des DataNodes.");
            // TODO séparer les cas d'erreur
        }
    }

    private static void registerFile(FileMetaData f) {
        try {
            NameNodeDaemon nnd = (NameNodeDaemon) Naming.lookup("//" + IPNameNode + ":" + portNameNode + "/NameNodeDaemon");
            nnd.registerFile(f);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            throw new NameNodeUnreachable("enregistrer un fichier.");
            // TODO séparer les cas d'erreur
            // TODO ?? maybe Throws plutot ??
        }
    }

    private static FileMetaData getFileMetadata(String fName) {
        try {
            NameNodeDaemon nnd = (NameNodeDaemon) Naming.lookup("//" + IPNameNode + ":" + portNameNode + "/NameNodeDaemon");
            return nnd.locateFile(fName);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            throw new NameNodeUnreachable("obtenir les metadonnées d'un fichier.");
            // TODO séparer les cas d'erreur
        }
    }

    private static FileMetaData[] getAllFileMetadata() {
        try {
            NameNodeDaemon nnd = (NameNodeDaemon) Naming.lookup("//" + IPNameNode + ":" + portNameNode + "/NameNodeDaemon");
            return nnd.getFiles();
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            throw new NameNodeUnreachable("lister les fichier.");
            // TODO séparer les cas d'erreur
        }
    }

    private static boolean removeFile(String fName){
        try {
            NameNodeDaemon nnd = (NameNodeDaemon) Naming.lookup("//" + IPNameNode + ":" + portNameNode + "/NameNodeDaemon");
            return nnd.removeFile(fName);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            throw new NameNodeUnreachable("lister les fichier.");
            // TODO séparer les cas d'erreur
        }
    }

    static class ConnexionBidirectionnel {
        public ObjectInputStream in;
        public ObjectOutputStream out;
        public Socket server;

        /* Ouvre une connexion */
        public ConnexionBidirectionnel(String ipAdress, int port) {
            System.out.println("HDFSClient : open connexion with "+ipAdress+":"+port);
            try {
                server = new Socket(ipAdress, port);
                in = new ObjectInputStream(server.getInputStream());
                out = new ObjectOutputStream(server.getOutputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // TODO lever une erreur?
                e.printStackTrace();
            }
        }

        public ConnexionBidirectionnel(String ipAdress, int port, Commande cmd) throws IOException {
            this(ipAdress, port);
            out.writeObject(cmd);
            out.flush();
        }

        /* Fermeture de la connexion */
        public void close() {
            try {
                in.close();
                out.close();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String parseOption(List<String> argsList, String optionIdentifier, String defaultValue){
        int i;
        if ((i = argsList.indexOf(optionIdentifier)) != -1) {
            if (argsList.size() <= i) {
                usage();
                System.exit(1);
            }
            String result = argsList.get(i + 1);
            argsList.remove(i + 1);
            argsList.remove(i);
            return result;
        }
        return defaultValue;
    }
}
