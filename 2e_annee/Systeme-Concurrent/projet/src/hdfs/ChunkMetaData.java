package hdfs;

import java.io.Serializable;

public class ChunkMetaData implements Serializable {
	/**
	 * 
	 * Cette classe contiennent les metedonnées d'un chunk Le NameNode charge ces
	 * données a partir d'un fichier json au demararge de NameNode , et il modifier
	 * ces données aprés chaque commande write ou delete affecté par le client Le
	 * NameNode envoie des objets de classe FileMetaData qui contient une liste de
	 * ChunkMetaDate au client quand il fait une commande ls ce qui permet au client
	 * d afficher les chunks d un fichier leurs tailles leurs emplacement dans le
	 * cluster , ..
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected NodeIdentifier[] nodeIds;
	protected String chunkLocalFname;
	protected long taille;

	
	public ChunkMetaData(NodeIdentifier idNode, long taille, String chunkLocalFname) {
		super();
		this.nodeIds=new NodeIdentifier[1];
		this.nodeIds[0] = idNode;
		this.taille = taille;
		this.chunkLocalFname = chunkLocalFname;
    }

	public ChunkMetaData(NodeIdentifier[] idNode, long taille, String chunkLocalFname) {
		super();
		this.nodeIds = idNode;
		this.taille = taille;
		this.chunkLocalFname = chunkLocalFname;
    }

    public NodeIdentifier[] getNodeIds() {
        return nodeIds;
    }

    public long getTaille() {
		return taille;
	}

    public void setTaille(long taille) {
        this.taille = taille;
    }

    @Override
	public String toString() {
		return "ChunkMetaData"; // [idNode=" + nodeIds.getNodeIP() + ":" + nodeIds.getHidoopPort() + ", chunkId=" + chunkId + ", taille=" + taille + "]";
	}

	public String getChunkLocalFname() {
		return chunkLocalFname;
	}

}
