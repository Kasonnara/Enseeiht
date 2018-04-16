package Entities;
import javax.persistence.*;

@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    protected int id;

    // /!\ Pour ne pas télécharger toute la base de donne a
    // ce paramètre est lazy, donc une jsp ne peut pas lister
    // les permissions depuis un objet topic ou utilisateur. Elles devront être
    // extraites avant par la servlet (par un get par exemple)
    @ManyToOne()
    protected Utilisateur  utilisateur;
    @ManyToOne()
    protected Topic topic;

    // True si l'utilisateur a le droit d'écrire sur le topic
    protected boolean droit_ecriture = true;
    // True si l'utilisateur a le droit de supprimer des messages autres que les siens
    protected boolean droit_suppression = false;
    // True si l'utilisateur a le droit d'inviter d'autre personnes sur le topic
    protected boolean droit_invitation = false;
    // True si l'utilisateur a le droit d'exclure quelqu'un du topic
    protected boolean droit_exclusion = false;

    public Permission() {
    }

    public Permission(boolean droit_ecriture, boolean droit_suppression, boolean droit_invitation, boolean droit_exclusion) {
        this.droit_ecriture = droit_ecriture;
        this.droit_suppression = droit_suppression;
        this.droit_invitation = droit_invitation;
        this.droit_exclusion = droit_exclusion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public boolean isDroit_ecriture() {
        return droit_ecriture;
    }

    public void setDroit_ecriture(boolean droit_ecriture) {
        this.droit_ecriture = droit_ecriture;
    }

    public boolean isDroit_suppression() {
        return droit_suppression;
    }

    public void setDroit_suppression(boolean droit_suppression) {
        this.droit_suppression = droit_suppression;
    }

    public boolean isDroit_invitation() {
        return droit_invitation;
    }

    public void setDroit_invitation(boolean droit_invitation) {
        this.droit_invitation = droit_invitation;
    }

    public boolean isDroit_exclusion() {
        return droit_exclusion;
    }

    public void setDroit_exclusion(boolean droit_exclusion) {
        this.droit_exclusion = droit_exclusion;
    }
}
