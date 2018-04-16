package hdfs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import formats.Format;
import formats.Format.Type;

public class FileMetaData implements Serializable {

    /**
     * Cette classe contiennent les metedonnées d'un fichier ( et les chunks de ce
     * fichiers graçe a la liste liChunks) Le NameNode charge ces données a partir
     * d'un fichier json au demararge de NameNode , et il modifier ces données aprés
     * chaque commande write ou delete affecté par le client Le NameNode envoie des
     * objets de cette classe au client quand il fait une commande ls ce qui permet
     * au client des informations sur un fichier son taille, son format ; et des
     * infos sur le chunks de ce fichiers
     */
    private static final long serialVersionUID = 1L;
    protected String fName;
    protected String reduceFName;
    protected long taille;
    protected Format.Type format;
    protected List<ChunkMetaData> liChunks;
    protected List<ChunkMetaData> reduceChunks;
    protected int replicationFactor;
    protected int reduceFactor;
    
    public FileMetaData(String fname, long taille, Type format, int replicationFactor,int reduceFactor) {
        this.liChunks = new ArrayList<ChunkMetaData>();
        this.reduceChunks = new ArrayList<ChunkMetaData>();
        this.fName = fname;
        this.reduceFName = fname + "-res-tmp"; // TODO adapt to multi reduce
        this.taille = taille;
        this.format = format;
        this.replicationFactor = replicationFactor;
        this.reduceFactor=reduceFactor;
    }

    public void addChunk(ChunkMetaData ch) {
        this.liChunks.add(ch);
        //this.reduceChunks.add(new ChunkMetaData(ch.getNodeIds(), 0, reduceFName)); // TODO adapt to multi reduce
    }

    

    public void addReduceChunk(ChunkMetaData ch) {
        this.reduceChunks.add(ch); // TODO adapt to multi reduce
    }

    @Override
    public String toString() {
        return "FileMetaData [fName=" + fName + ", taille=" + taille + ", format=" + format + ", nbChunks=" + liChunks.size()
                + "]";
    }

    public List<ChunkMetaData> getLiChunks() {
        return liChunks;
    }

    public String getfName() {
        return fName;
    }

    public String getReduceFName() {
        return reduceFName;
    }

    public long getTaille() {
        return taille;
    }

    public void setTaille(long newTaille) {
        taille = newTaille;
    }

    public Format.Type getFormat() {
        return format;
    }

    public void setFormat(Format.Type format) {
        this.format = format;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public List<ChunkMetaData> getReduceChunks() {
        return reduceChunks;
    }

    public int getNumberOfChunk() {
        return liChunks.size();
    }

    public int getNumberOfReduceChunk() {
        return reduceChunks.size();
    }

	public int getReduceFactor() {
		return reduceFactor;
	}

	public void setReduceFactor(int reduceFactor) {
		this.reduceFactor = reduceFactor;
	}
    
    
    
}
