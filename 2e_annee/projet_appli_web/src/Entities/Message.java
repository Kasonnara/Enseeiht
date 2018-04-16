package Entities;
import javax.persistence.*;

@Entity
public class Message {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	int id;

	private int numero; // position du message dans le topic
	@ManyToOne(fetch=FetchType.EAGER)
	private Utilisateur auteur;
	private int jour;
	private int mois;
	private int an;
	private String contenu;

	@ManyToOne()
	private Topic topic;
	
	public Message(Utilisateur auteur, int jour, int mois, int an, String contenu) {

		this.auteur = auteur;
		this.jour = jour;
		this.mois = mois;
		this.an = an;
		this.contenu = contenu;
	}

	public Message() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
		this.numero= topic.getNbMessages();
	}

	public int getNumero() {
		return this.numero;
	}
	
	public Utilisateur getAuteur() {
		return this.auteur;
	}
	
	public int getJour() {
		return this.jour;
	}
	public int getMois() {
		return this.mois;
	}
	public int getAn() {
		return this.an;
	}
	
	public String getContenu() {
		return this.contenu;
	}
	
	public void setNumero(int numero) {
		this.numero = numero;
	}
	
	public void setAuteur(Utilisateur auteur) {
		this.auteur = auteur;
	}
	public void setJour(int jour) {
		this.jour = jour;
	}
	public void setMois(int mois) {
		this.mois = mois;
	}
	public void setAn(int an) {
		this.an = an;
	}
	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

}
