package hdfs;

import java.io.Serializable;

import formats.Format;

public class Commande implements Serializable{
	
	/**
	 * cette classe facilite la communication entre le client et le serveur 
	 * chaque commande a un code qui précise l'action souhaitée
	 * et un nom qui est le nom de fragement de fichier à lire ou à ecrire ou à supprimer selon le cas
	 *  et le format de fichier
	 *  le client envoie un objet de cette classe au début de chaque communication avec un serveur
	 */
	private static final long serialVersionUID = 1L;

	public static enum CommandeCode { CMD_READ , CMD_WRITE , CMD_DELETE };
	CommandeCode code;
	String name;
	Format.Type type;
	
	public Commande(CommandeCode code, String name,Format.Type type) {
		super();
		this.code = code;
		this.name = name;
		this.type = type;
	}
	
	public Commande() {
	}
	
	public Commande(CommandeCode code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public Format.Type getType() {
		return type;
	}

	public void setType(Format.Type type) {
		this.type = type;
	}

	public CommandeCode getCode() {
		return code;
	}
	
	public void setCode(CommandeCode code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Commande [code=" + code + ", name=" + name + "]";
	}

}
