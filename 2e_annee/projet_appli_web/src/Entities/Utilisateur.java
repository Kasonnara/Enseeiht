package Entities;

import java.util.Calendar;
import java.util.List;

import javax.persistence.*;

@Entity
public class Utilisateur {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	int id;
	
	private String nom;
	private String prenom;
	private String pseudo;
	private String mdp;
	private boolean isAdmin;
	private Calendar finVIP = null;
	private boolean VIP ;


	@OneToMany(mappedBy="auteur")
	private List<Message> messages;
    @OneToMany(mappedBy="createur")
    private List<Topic> topics;
	@OneToMany(mappedBy="utilisateur")
	private List<Permission> permissions;
	@OneToMany(mappedBy="utilisateur", fetch= FetchType.EAGER)
	private List<Invitation> invitations;
	@OneToMany(mappedBy = "acheteur", fetch= FetchType.LAZY)
	private List<LogPayement> logsPayement;


	public Utilisateur(){}

	public Utilisateur(String nom, String prenom, String pseudo, String mdp) {
		this.nom = nom;
		this.prenom = prenom;
		this.pseudo = pseudo;
		this.mdp = mdp;
		this.isAdmin = this.pseudo.equals("admin");
		this.VIP = this.isAdmin;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}
	public String getNom() {
		return this.nom;
	}
	public String getPrenom() {
		return this.prenom;
	}
	public String getPseudo() {
		return this.pseudo;
	}
	public String getMdp() {
		return this.mdp;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public List<Invitation> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<Invitation> invitations) {
		this.invitations = invitations;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public boolean isVIP() {
		return isAdmin || (finVIP != null); // && finVIP.after(Calendar.getInstance()));
	}

	public void setVIP(boolean vip) {
		this.VIP = vip;
	}

	public Calendar getFinVIP() {
		return this.finVIP;
	}
	
	public void setFinVIP(Calendar d) {
		this.finVIP = d;
		this.setVIP(true);
	}

	public List<LogPayement> getLogsPayement() {
		return logsPayement;
	}

	public void setLogsPayement(List<LogPayement> logsPayement) {
		this.logsPayement = logsPayement;
	}
}
