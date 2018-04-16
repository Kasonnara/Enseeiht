package facade;

import java.util.*;

import javax.ejb.Singleton;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.rmi.CORBA.Util;

import Entities.*;


@Singleton
public class Facade {
    @PersistenceContext
    EntityManager em;


    public Facade(){
        //Utilisateur defautAdmin = new Utilisateur("admin", "admin", "admin", "admin"); // TODO supprimer en prod
        //this.em.persist(defautAdmin);
    }

    public boolean ajoutUtilisateur(String nom, String prenom, String pseudo, String mdp) {
        return ajoutUtilisateurAndReturn(nom, prenom, pseudo, mdp) != null;
    }
    public Utilisateur ajoutUtilisateurAndReturn(String nom, String prenom, String pseudo, String mdp) {
        // Vérifier que l'utilisateur n'existe pas
        if (rechercherUtilisateur(pseudo) != null) {
            return null;
        }

        Utilisateur u = new Utilisateur(nom, prenom, pseudo, mdp);
        this.em.persist(u);
        return u;
    }

    // A voir quel signature est la plus pratique selon la BDD (potentiellement les deux selon les cas)
    public void SupprimerUtilisateur(String pseudo) {
        this.em.createNativeQuery("remove * from Utilisateur u where u.pseudo = :pseudo").setParameter("pseudo", pseudo);
    }
    public void SupprimerUtilisateur(int identifier){
        this.em.createNativeQuery("remove * from Utilisateur u where u.id = :id").setParameter("id", identifier);
    }

    public Utilisateur rechercherUtilisateur(String pseudo){
        TypedQuery<Utilisateur> req = em.createQuery("select u from Utilisateur u where u.pseudo = :pseudo",
                Utilisateur.class).setParameter("pseudo", pseudo).setMaxResults(1);
        List<Utilisateur> results = req.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
    
    public Utilisateur rechercherUtilisateur(int id){
        TypedQuery<Utilisateur> req = em.createQuery("select u from Utilisateur u where u.id = :id",
                Utilisateur.class).setParameter("id", id).setMaxResults(1);
        List<Utilisateur> results = req.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    public List<Utilisateur> listerUtilisateurs(){
        TypedQuery<Utilisateur> req = em.createQuery("select u from Utilisateur u",
                Utilisateur.class);
        return req.getResultList();
    }

    /**
     * Liste les topics visibles
     */
    public List<Topic> ListerTopics(){return em.createQuery("select t from Topic t",
            Topic.class).getResultList();}
    public List<Topic> ListerTopics(Utilisateur user){
        if (user.isVIP()){
            return ListerTopics();
        }
        return em.createQuery("select distinct t from Topic t, Permission p " +
                        "where (t.createur.id = :userId or (t.id = p.topic.id and p.utilisateur.id = :userId))", // or t.isPublic
            Topic.class)
                .setParameter("userId", user.getId())
                .setParameter("userId", user.getId())
                .getResultList();
    }

    public Topic getTopic(int topicId){
        TypedQuery<Topic> req = em.createQuery("select t from Topic t where t.id = :identifier",
                Topic.class).setParameter("identifier", topicId).setMaxResults(1);
        List<Topic> results = req.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
    public Topic getTopic(String titre){
        TypedQuery<Topic> req = em.createQuery("select t from Topic t where t.titre = :identifier",
                Topic.class).setParameter("identifier", titre).setMaxResults(1);
        List<Topic> results = req.getResultList();
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    public Topic ajoutTopic(String titre){
        return ajoutTopic(titre, null);
    }
    public Topic ajoutTopic(String titre, Utilisateur proprietaire){
        return ajoutTopic(titre, proprietaire, true);
    }
    public Topic ajoutTopic(String titre, Utilisateur proprietaire, boolean isPublic){
        Topic t = new Topic(titre, proprietaire);
        t.setPublic(isPublic);
        em.persist(t);

        Permission p = new Permission(true, true, true, true);
        em.persist(p);
        p.setTopic(t);
        p.setUtilisateur(proprietaire);
        return t;
    }

    public void supprimerTopic(int identifer){
        this.em.createNativeQuery("remove * from Topic t where t.id = :identifier").setParameter("identifier", identifer);
    }

    /**
     * Liste les message d'un topic donné
     */
    public List<Message> ListerTopicMessages(int topicId){
        return getTopic(topicId).getMessages();
    }

    // A voir quel signature est la plus pratique selon la BDD (potentiellement les deux selon les cas)
    public void ajoutMessage(int topicId, String pseudoAuteur, int jour, int mois, int an, String contenu) {
        Topic t = getTopic(topicId);
        Utilisateur auteur = rechercherUtilisateur(pseudoAuteur);
        Message m = new Message(null, jour, mois, an, contenu);
        em.persist(m);
        m.setAuteur(auteur);
        m.setTopic(t);
    }

    // A voir quel signature est la plus pratique selon la BDD (potentiellement les deux selon les cas)
    public void supprimerMessage(int identifier) {
        this.em.createNativeQuery("remove * from Message m where m.id = :identifier").setParameter("identifier", identifier);
    }
    public void supprimerMessage(int topicIdentifier, int numero) {
        this.em.createNativeQuery("remove * from Message m where m.topic.id = :topicId and m.numero = :num")
                .setParameter("topicId", topicIdentifier)
                .setParameter("num", numero);
    }

    public boolean doInvitPermExist(Utilisateur inviteUser, Topic t){
        return em.createQuery("select p from Permission p " +
                        "where (p.topic.id = :topicId and p.utilisateur.id = :userId)",
                Permission.class)
                .setParameter("userId", inviteUser.getId())
                .setParameter("topicId", t.getId())
                .getResultList()
                .size() > 0
                ||
                em.createQuery("select i from Invitation i " +
                                "where (i.topic.id = :topicId and i.utilisateur.id = :userId)",
                        Invitation.class)
                .setParameter("userId", inviteUser.getId())
                .setParameter("topicId", t.getId())
                .getResultList()
                .size() > 0
                ;
    }

    public boolean invite(Utilisateur inviteUser, Topic t) {
        // Vérifier que la personne n'a pas déjà été invitée
        if (doInvitPermExist(inviteUser, t)) return false;
        Invitation i = new Invitation(t, inviteUser);
        i.setTopic(t);
        this.em.persist(i);
        return true;
    }
    public boolean validerInvite(int perm_topic, int perm_user){
        Invitation i = getInvitation(perm_topic, perm_user);
        if (i==null) return false;
        Permission p = new Permission(i.isDroit_ecriture(), i.isDroit_suppression(), i.isDroit_invitation(), i.isDroit_exclusion());
        p.setTopic(getTopic(perm_topic));
        p.setUtilisateur(rechercherUtilisateur(perm_user));
        em.persist(p);
        supprimerInvitation(perm_topic, perm_user);
        return true;
    }

    public Invitation getInvitation(int topicId, int userId) {
        List<Invitation> i = em.createQuery("select i from Invitation i " +
                        "where i.utilisateur.id = :userId and i.topic.id = :topicId",
                Invitation.class)
                .setParameter("topicId", topicId)
                .setParameter("userId", userId)
                .getResultList();
        if (i.size()==0){
            return null;
        }else{
            return i.get(0);
        }
    }
    public void supprimerInvitation(int topicId, int userId){
        /*TODO bug
        em.createNativeQuery("DELETE Invitation i " +
                "where i.utilisateur.id = :userId and i.topic.id = :topicId")
                .setParameter("topicId", topicId)
                .setParameter("userId", userId)
        .executeUpdate();
        em.flush();*/
    }

    public List<Utilisateur> getInvitedUser(Topic t) {
        return em.createQuery("select distinct u from Utilisateur u, Topic t, Invitation i " +
                        "where i.utilisateur.id = u.id and i.topic.id = t.id and t.id = :topicId",
                Utilisateur.class)
                .setParameter("topicId", t.getId())
                .getResultList();
    }
    public List<Invitation> getInvitation(Utilisateur u) {
        return em.createQuery("select distinct i from Invitation i " +
                        "where i.utilisateur.id = :userId",
                Invitation.class)
                .setParameter("userId", u.getId())
                .getResultList();
    }
    public List<Topic> getInvitedTopics(Utilisateur u) {
        return em.createQuery("select distinct i.topic from Invitation i " +
                        "where i.utilisateur.id = :userId",
                Topic.class)
                .setParameter("userId", u.getId())
                .getResultList();
    }

    public Permission getPermission(int topicId, int userId) {
        List<Permission> p = em.createQuery("select distinct p from Permission p " +
                        "where p.utilisateur.id = :userId and p.topic.id = :topicId",
                Permission.class)
                .setParameter("topicId", topicId)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultList();
        if (p==null || p.size()==0){
            return null;
        }else{
            return p.get(0);
        }
    }
    public void supprimerPermission(int topicId, int userId){
        em.createNativeQuery("DELETE p from Permission p " +
                "where p.utilisateur.id = :userId and p.topic.id = :topicId")
                .setParameter("topicId", topicId)
                .setParameter("userId", userId);
    }
    public List<Utilisateur> getPermtedUsers(Topic t) {
        return em.createQuery("select distinct u from Utilisateur u, Topic t, Permission p " +
                        "where p.utilisateur.id = u.id and p.topic.id = t.id and t.id = :topicId",
                Utilisateur.class)
                .setParameter("topicId", t.getId())
                .getResultList();
    }
    public List<Utilisateur> getPermitions(Utilisateur u) {
        return em.createQuery("select distinct p from Permission p " +
                        "where p.utilisateur.id = :userId",
                Utilisateur.class)
                .setParameter("userId", u.getId())
                .getResultList();
    }
    public List<Topic> getPermitedTopics(Utilisateur u) {
        return em.createQuery("select distinct p.topic from Permission p " +
                        "where p.utilisateur.id = :userId",
                Topic.class)
                .setParameter("userId", u.getId())
                .getResultList();
    }


    public void ajoutLogPayement(Utilisateur u, Calendar d, int v) {
    	LogPayement log = new LogPayement(u, d, v);
    	em.persist(log);
    }
    
    public void setFinVIP(Utilisateur u, Calendar d) {
    	u.setFinVIP(d);
    	u.setVIP(u.isVIP());
    }

    public List<Label> listerLabels(){
        return em.createQuery("select l from Label l", Label.class).getResultList();
    }
    public Collection<Label> listerLabel(Topic t){
        List<Collection> ll = (em.createQuery("select t.labels from Topic t " +
                "where (t.id = :topicId)", Collection.class)
                .setParameter("topicId", t.getId())
                .getResultList());
        if (ll.size() == 0){
            return new LinkedList<Label>();
        }else{
            return ll.get(0);
        }
    }
    public Label getLabel(String texte){
        List<Label> p = em.createQuery("select distinct l from Label l" +
                        " where l.text = :labelId",
                Label.class)
                .setParameter("labelId", texte)
                .setMaxResults(1)
                .getResultList();
        if (p==null || p.size()==0){
            return null;
        }else{
            return p.get(0);
        }
    }
    public Label getLabel(int labelId){
        List<Label> p = em.createQuery("select distinct l from Label l" +
                        " where l.id = :labelId",
                Label.class)
                .setParameter("labelId", labelId)
                .setMaxResults(1)
                .getResultList();
        if (p==null || p.size()==0){
            return null;
        }else{
            return p.get(0);
        }
    }
    public void ajouterLabel(String name, String html_color){
        Label l = new Label(name, html_color);
        em.persist(l);
    }
    public void lierLabel(int labelId, int topicId){
        Label l = getLabel(labelId);
        Topic t = getTopic(topicId);
        if (l==null || t==null){
            return;
        }
        l.getTopics().add(t);
    }

}