package hdfs;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.net.MalformedURLException;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.traversal.NodeIterator;

import formats.Format;
import formats.RemoteFormatImpl;


/*
*
 * ------le NameNode charge les ports et les adresses ip des dataNode depuis un fichier-------------
 * ----------d'un config donné comme paramétre au lancement de NameNode-----------
 *	il charge des metadonnées sur les fichiers  et les Chunks de chaque fichier a partir d'un fichier json
 * puis il se met à  l'écoute sur un hdfsPort
 * quand il reçoit une connexion d'un client il lance une Thread  NameNodeInterction qui traite cette connexion 
 * et il se met en attente d'un autre client
 * 
 */
public class NameNode extends UnicastRemoteObject implements NameNodeDaemon, DataNameNodeDaemon{

	public static final String nameFileMetaData ="chunks.metadata" ;
	public static int portNameNode;
	public static String pathMetaData;
	public static Map<String,NodeIdentifier> dataNodes = new HashMap<>();
	public static Map<String,FileMetaData> mapFileName = new HashMap<String,FileMetaData>();
	public static Map<String,FileMetaData> reduceRemap = new HashMap<>();

    protected NameNode() throws RemoteException {
        // TODO retirer les static
    }

    private static void usage() {
		System.out.println("Usage: java NameNode <path to config name node>");	
	}
	
	public static void main(String[] args)  {
		if (args.length != 1) {
			usage();
			return;
		}		
		loadConfig(args[0]);	
		loadMetaData();

        try {
            int rmiPort = portNameNode;
            LocateRegistry.createRegistry(rmiPort);
            NameNode nameNodeInstance = new NameNode();
            Naming.rebind("//localhost:" + rmiPort + "/NameNodeDaemon", nameNodeInstance);
            Naming.rebind("//localhost:" + rmiPort + "/DataNameNodeDaemon", nameNodeInstance);
            System.out.println("NameNode RMI en écoute sur le hdfsPort " + rmiPort);
        } catch (RemoteException | MalformedURLException e) {
		    // TODO throw error
            e.printStackTrace();
        }

        /*ServerSocket server=null;
		 try {
			server=new ServerSocket(portNameNode);//Serveur en écoute
			System.out.println("NameNode en écoute sur le hdfsPort "+portNameNode);
			Socket client;
			while(true){
				 client=server.accept();
				 //chaque interaction avec client est traité par une Thread 
				 NameNodeIntercation ic =new NameNodeIntercation(client);
				 new Thread(ic).start();
			 }
		} catch (IOException e) {
			System.out.println("Error : connection failed");
			e.printStackTrace();
		}finally {
			if(server!=null) {
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
	}
	

	//chargement des metaDonnees depuis un fichier json et remplissage le HashMap mapFileName
	//les metaDonnées contiennent des informations sur les fichiers (nom,taille,format)
	// et les chunks de chaque fichiers (idChunk,taille,nodeDeChunk)
	public static void loadMetaData() {
        // TODO add reduceRemap
//
//        JSONParser parser = new JSONParser();
//        if(!new File(pathMetaData+nameFileMetaData).exists()) 
//        {
//    		JSONObject main=new JSONObject();
//    		JSONArray listFile = new JSONArray();
//    		main.put("array", listFile);
//	        try (FileWriter file = new FileWriter(pathMetaData+nameFileMetaData)) {
//	            file.write(main.toJSONString());
//	            file.flush();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//        } 
//
//
//        try {
//        	Object obj=parser.parse(new FileReader(pathMetaData+nameFileMetaData));
//        	JSONObject main=(JSONObject)obj;
//        	JSONArray listFile = (JSONArray)main.get("array");
//        	Iterator<JSONObject> iteratorFile = listFile.iterator();
//            while (iteratorFile.hasNext()) {
//            	JSONObject objFm=iteratorFile.next();
//            	String fileId=(String)objFm.get("fileId");
//            	long taille=(long)objFm.get("taille");
//            	String formatStr=(String)objFm.get("format");
//            	Format.Type format=null;
//            	switch(formatStr) {
//            	case "LINE":
//            		format=Format.Type.LINE;
//            		break;
//            	case "KV":
//            		format=Format.Type.KV;
//            		break;
//            	default:
//            		throw new RuntimeException("Invalid Format");
//            	}
//                int replicationFactor = (int)objFm.get("replicationFactor");
//
//                FileMetaData fm=new FileMetaData(fileId, taille, format, replicationFactor);
//
//                JSONArray listChunk = (JSONArray)objFm.get("liChunks");
//            	Iterator<JSONObject> iteratorChunk = listChunk.iterator();
//                while (iteratorChunk.hasNext()) {
//                    JSONObject objChunk = iteratorChunk.next();
//                    long tailleCh = (Long) objChunk.get("taille");
//                    String sLocFname = (String) objChunk.get("sourceLocalFname");
//
//                    JSONArray listNode = (JSONArray)objChunk.get("nodeIds");
//                    Iterator<JSONObject> iteratorNodes = listNode.iterator();
//                    List<NodeIdentifier> nodes = new LinkedList<>();
//                    while (iteratorNodes.hasNext()) {
//                        JSONObject objCh = iteratorNodes.next();
//                        NodeIdentifier nodeId =new NodeIdentifier((String)objCh.get("nodeIP"), (int)objCh.get("nodeHidoopPort"), (int)objCh.get("nodeHdfsPort"),(String) objCh.get("nodePathData"));
//                        nodes.add(nodeId);
//                    }
//                    fm.addChunk(new ChunkMetaData(nodes.toArray(new NodeIdentifier[nodes.size()]), tailleCh, sLocFname));
//                }
//                mapFileName.put(fm.getfName(),fm);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//            System.out.println("Error parsing "+ pathMetaData+nameFileMetaData);
//
//        }
	}
	
	//écriture des metaDonnees dans un fichier json 
	//les metaDonnées se trouvent sur le HashMap mapFileName
	public static void writeMetaData() {

//        // TODO add reduceRemap
//
//		JSONObject main=new JSONObject();
//		JSONArray listFile = new JSONArray();
//        for(Map.Entry m:mapFileName.entrySet()) {
//            FileMetaData fm = (FileMetaData) m.getValue();
//            JSONObject objFile = new JSONObject();
//            objFile.put("fileId", fm.getfName());
//            objFile.put("format", fm.getFormat().toString());
//            objFile.put("taille", fm.getTaille());
//            objFile.put("replicationFactor", fm.replicationFactor);
//
//            JSONArray listChunk = new JSONArray();
//            for (ChunkMetaData chm : fm.getLiChunks()) {
//                JSONObject objChunk = new JSONObject();
//                objChunk.put("taille", chm.getTaille());
//                objChunk.put("sourceLocalFname", chm.getChunkLocalFname());
//
//                JSONArray objNodes = new JSONArray();
//                for (NodeIdentifier ni : chm.getNodeIds()) {
//                    JSONObject objNd = new JSONObject();
//                    objNd.put("nodeIP",ni.getNodeIP());
//                    objNd.put("nodeHdfsPort",ni.getHdfsPort());
//                    objNd.put("nodePathData",ni.getPathData());
//                    objNd.put("nodeHidoopPort",ni.getHidoopPort());
//                    objNodes.add(objNd);
//                }
//                objChunk.put("nodeIds", objNodes);
//                listChunk.add(objChunk);
//            }
//            objFile.put("liChunks", listChunk);
//            listFile.add(objFile);
//
//        }
//        main.put("array", listFile);
//	        
//        try (FileWriter file = new FileWriter(pathMetaData+nameFileMetaData)) {
//
//            file.write(main.toJSONString());
//            file.flush();
//            file.close();
//            System.out.println("Metadata Saved to " + pathMetaData+nameFileMetaData);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Fail to save metadata");
//        }
	}
	
	//chargement des  configurations de cluster depuis un fichier properties qui contient la configuration
	//et remplissage de HashMap dataNodes
	//le fichier config doit contenir le hdfsPort de NameNode le path de dossier ou se trouve le fichier json de metaData
	//le hdfsPort et l'adress ip de chaque dataNode
	public static void loadConfig(String pathFile) {
		Properties prop = new Properties();

		try {
			// load a properties file
			prop.load( new FileInputStream(pathFile));
			// get the property value and print it out
			portNameNode=Integer.parseInt(prop.getProperty("portNameNode"));
			pathMetaData=prop.getProperty("pathMetaData");
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
	}


	@Override
	public int getNumberOfChunk(String fname) throws RemoteException {
        if (reduceRemap.containsKey(fname)){
            return reduceRemap.get(fname).getNumberOfReduceChunk();
        }
        else if (!mapFileName.containsKey(fname)){
            // TODO Throw an error
            return -1;
        }
        return mapFileName.get(fname).getNumberOfChunk();
	}
	@Override
	public ChunkMetaData locateChunk(String fname, int chunkId) throws RemoteException {
        if (reduceRemap.containsKey(fname)){
            System.out.println("location request result "+fname +", "+(chunkId+1)+"/"+this.getNumberOfChunk(fname));
            return reduceRemap.get(fname).getReduceChunks().get(chunkId);
        }
        else if (!mapFileName.containsKey(fname)){
            // TODO Throw an error
            System.out.println("location impossible "+fname +".");
            return null;
        }
        System.out.println("location request "+fname +", "+(chunkId+1)+"/"+this.getNumberOfChunk(fname));
        return mapFileName.get(fname).getLiChunks().get(chunkId);
    }

    @Override
    public void registerFile(FileMetaData fileIN) throws RemoteException {
    	fileIN=addChunksReduce(fileIN);
	    System.out.println("Enregistrement d'un fichier : " + fileIN.getfName() + " et son resultat " + fileIN.getReduceFName());
        mapFileName.put(fileIN.getfName(), fileIN);
        
        
 
        reduceRemap.put(fileIN.getReduceFName(), fileIN);
        NameNode.writeMetaData();
    }
    
    private FileMetaData addChunksReduce(FileMetaData fileIN) {
    	List<NodeIdentifier> liNodeReduces=choisirNodeForReduce(fileIN.getReduceFactor());
    	int i=0;
    	for(NodeIdentifier ni : liNodeReduces) {
    		fileIN.addReduceChunk(new ChunkMetaData(ni, 0, fileIN.getfName()+"red"+i));
    		i++;
    	}
		return fileIN;
	}

	private List<NodeIdentifier> choisirNodeForReduce(int reduceFactor) {
		int i=1;
		List<NodeIdentifier> liNI=new ArrayList<NodeIdentifier>();
		for(NodeIdentifier ni:dataNodes.values()) {
			if(i>reduceFactor) {
				break;
			}
			liNI.add(ni);
			i++;
		}
		return liNI;
    }
    
    @Override
	public boolean removeFile(String fileName) throws RemoteException {
        if (mapFileName.containsKey(fileName)){
            System.out.println("Suppression d'un fichier : " + fileName);
            reduceRemap.remove( mapFileName.get(fileName).getReduceFName());
            mapFileName.remove(fileName);
            NameNode.writeMetaData();
            return true;
        } else {
            System.out.println("Le fichier a supprimer " + fileName + " n'est pas présent." );
            return false;
        }
	}

    @Override
    public FileMetaData locateFile(String fname) throws RemoteException {
        if (reduceRemap.containsKey(fname)){
            System.out.println("location request result "+fname +", full file");
            return reduceRemap.get(fname);
        }
        else if (!mapFileName.containsKey(fname)){
            // TODO Throw an error
            System.out.println("location impossible "+fname +", full file");
            return null;
        }
        System.out.println("location request "+fname +", full file");
        return mapFileName.get(fname);
    }

    @Override
    public FileMetaData[] getFiles() throws RemoteException {
	    System.out.println("Requete de la liste des fichiers.");
        return mapFileName.values()
                .toArray(new FileMetaData[mapFileName.size()]);
    }

    @Override
	public void registerDaemon(NodeIdentifier newDaemon) throws RemoteException {
        System.out.println("Enregistrement d'un nouveau démon " + newDaemon.getNodeIP() + ":" + newDaemon.getHidoopPort()+"|" + newDaemon.getHdfsPort());
		dataNodes.put(NodeToKey(newDaemon),newDaemon);
	}

	@Override
	public NodeIdentifier[] getDaemons() throws RemoteException {
        System.out.println("Requete de la liste des démons. (nombre de démon "+ dataNodes.size() +")");
        return dataNodes.values().toArray(new NodeIdentifier[dataNodes.size()]);
    }

	@Override
	public boolean removeDaemon(NodeIdentifier removedDaemon) throws RemoteException {
    	String key = NodeToKey(removedDaemon);
    	if (dataNodes.containsKey(key)){
			System.out.println("Requete suppression d'un démons.");
			dataNodes.remove(key);
			return true;
		} else {
			System.out.println("Impossible de supprimer le démon!");
			// TODO throw an error ?
			return false;
		}
	}

	private String NodeToKey(NodeIdentifier node){
    	return node.getNodeIP() + ":" + node.getHdfsPort();
	}


}
