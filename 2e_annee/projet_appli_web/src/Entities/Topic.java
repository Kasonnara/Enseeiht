package Entities;

import facade.Facade;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Topic {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	int id;
	
	private String titre;
	@ManyToOne(fetch=FetchType.EAGER)
	private Utilisateur createur;
	@OneToMany(mappedBy = "topic", fetch=FetchType.EAGER)
	private List<Message> messages;
	private boolean isPublic = true;
	@OneToMany(mappedBy = "topic")
	private List<Invitation> invitations;
	@OneToMany(mappedBy = "topic")
	private List<Permission> permissions;

	@ManyToMany
	private List<Label> labels;

	public Topic(){
		this.titre = "No Title";
		this.createur = null;
		messages = new ArrayList<>();
	}
	public Topic(String titre, Utilisateur createur) {
		this.titre = titre;
		this.createur = createur;
		messages = new ArrayList<>();
	}
	
	public String getTitre() {
		return this.titre;
	}
	public Utilisateur getCreateur() {
		return this.createur;
	}
	public List<Message> getMessages() {
		return this.messages;
	}
	
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public void setCreateur(Utilisateur createur) {
		this.createur = createur;
	}
	public void ajoutMessage(Message message) {
		int n = messages.size();
		messages.add(message);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	public Message getMessage(int numero) {
		return messages.get(numero);
	}
	
	public int getNbMessages() {
		return messages.size();
	}

	public void ajoutMessage(String utilisateur, int jour, int mois, int an, String contenu){}

    public int getId() {
        return id;
    }

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean aPublic) {
		isPublic = aPublic;
	}

	public boolean canRead(Utilisateur user, Facade f){
		if (isPublic() || user.getId() == this.getCreateur().getId() || user.isVIP()) {
			return true;
		}
		Permission p = f.getPermission(this.id, user.id);
		return p!=null;
	}
	public boolean canWrite(Utilisateur user, Facade f){
		if (isPublic() || user.getId() == this.getCreateur().getId() || user.isVIP()) {
			return true;
		}
		Permission p = f.getPermission(this.id, user.id);
		return p!=null && p.isDroit_ecriture();
	}
	public boolean canDeleteTopic(Utilisateur user){
		return user.getId() == this.getCreateur().getId();
	}
	public boolean canDeleteMessage(Utilisateur user, Message m, Facade f){
		if (user.getId() == this.getCreateur().getId() || user.getId() == m.getAuteur().getId()  || user.isAdmin()) {
			return true;
		}
		Permission p = f.getPermission(this.id, user.id);
		return p!=null && p.isDroit_suppression();
	}
	public boolean canKick(Utilisateur user, Facade f){
		if (user.getId() == this.getCreateur().getId() || user.isAdmin()) {
			return true;
		}
		Permission p = f.getPermission(this.id, user.id);
		return p!=null && p.isDroit_exclusion();
	}
	public boolean canInvite(Utilisateur user, Facade f){
		if (user.getId() == this.getCreateur().getId() || user.isAdmin()) {
			return true;
		}
		Permission p = f.getPermission(this.id, user.id);
		return p!=null && p.isDroit_invitation();
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

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}
}
