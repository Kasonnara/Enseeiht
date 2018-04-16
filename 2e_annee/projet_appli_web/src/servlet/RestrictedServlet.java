package servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Entities.Invitation;
import Entities.Label;
import Entities.Topic;
import Entities.Utilisateur;

/**
 * Servelet regroupant toutes les actions necessitant d'être connecté.
 *      La vérification que l'utilisateur est connecté est automatique.
 */

@WebServlet("/Restricted")
public class RestrictedServlet extends Serv{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        // Vérification de la session de l'utilisateur
        HttpSession session = request.getSession();
        String op = request.getParameter("op");
        int topicId;
        int userID=(int) session.getAttribute("sessionUserID");
        Utilisateur user = f.rechercherUtilisateur(userID);
        if(op!=null) {
            switch (op) {
                case "accueil":
                    DisplayTopicList(user, request, response);
                    break;
                case "deconnexion":
                    session.setAttribute("sessionUserID", null);
                    session.setAttribute("user", null);
                    session.setAttribute("admin", null);
                    request.getRequestDispatcher("bienvenue.html").forward(request, response);
                    break;
                case "Vcreationtopic":
                    String[] labels = request.getParameter("labels").split(",");
                    String titre = request.getParameter("titre");
                    boolean isPublic = request.getParameter("isPublic").equals("true");
                    Topic t2 = f.ajoutTopic(titre, user, isPublic);
                    for (String labelName : labels){
                        Label l = f.getLabel(labelName);
                        if (l!=null) {
                            f.lierLabel(l.getId(), t2.getId());
                        }
                    }
                    DisplayTopicList(user, request, response);
                    break;
                case "moncompte":
                    DisplayMonCompte(user, request, response);
                    break;
                case "creationtopic":
                    request.getRequestDispatcher("creation_topic.html").forward(request, response);
                    break;
                case "afficherTopic":
                    topicId = Integer.parseInt(request.getParameter("topicId"));
                    DisplayTopic(topicId, user, request, response);
                    break;
                case "Vcommentaire":
                    //Date date = null;
                    Calendar cal = Calendar.getInstance();
                    //cal.setTime(date);
                    int an = cal.get(Calendar.YEAR);
                    int mois = cal.get(Calendar.MONTH);
                    int jour = cal.get(Calendar.DAY_OF_MONTH);
                    String contenu = request.getParameter("contenu");
                    topicId = Integer.parseInt(request.getParameter("topicId"));
                    f.ajoutMessage(topicId, user.getPseudo(), jour, mois + 1, an, contenu);
                    DisplayTopic(topicId, user, request, response);
                    break;

                case "Vinvite":
                    Utilisateur inviteUser = f.rechercherUtilisateur(request.getParameter("invitePseudo"));
                    Topic t = f.getTopic(Integer.parseInt(request.getParameter("topicId")));
                    if (inviteUser == null){
                        request.setAttribute("InviteError", "Cet utilisateur n'existe pas.");
                    } else if (t.canInvite(user, f)) {
                        if(f.invite(inviteUser, t));
                        else request.setAttribute("InviteError", "Personne deja invitee.");
                    }else{
                        request.setAttribute("InviteError", "Vous n'avez pas les permissions requises.");
                        //TODO fail, pas le droit ou le pseudo invité n'existe pas
                    }
                    DisplayTopic(t.getId(), user, request, response);
                case "devenirVIP":
                	request.getRequestDispatcher("devenir_VIP.html").forward(request, response);
                    break;
               	case "Vpaiement":
                	String nom_u = request.getParameter("utilisateur");
                	Utilisateur u = f.rechercherUtilisateur(nom_u);
                    Calendar d = Calendar.getInstance();
                	int v = Integer.parseInt(request.getParameter("duree"));
                	f.ajoutLogPayement(u, d, v);
                	Calendar fin = d;
                	fin.add(Calendar.DATE, v);
                	f.setFinVIP(u, fin);
                    DisplayMonCompte(user, request, response);
                    break;
                case "Vdelete_permission":
                    int perm_topic = Integer.parseInt(request.getParameter("perm_topic"));
                    int perm_user = Integer.parseInt(request.getParameter("perm_user"));
                    // verifier que l'utilisateur a les droits
                    if (user.isAdmin() || user.getId() == perm_user || f.getTopic(perm_topic).canInvite(f.rechercherUtilisateur(perm_user),f)) {
                        f.supprimerPermission(perm_topic, perm_user);
                        f.supprimerInvitation(perm_topic, perm_user);
                        DisplayMonCompte(user, request, response);
                    }else{
                        DisplayErrorPage("Vous n'avez pas les droit pour cela.", "Public?op=bienvenue", request, response);
                    }
                    break;
                case "Vaccepter_invitation":

                    int perm_topic2 = Integer.parseInt(request.getParameter("perm_topic"));
                    int perm_user2 = Integer.parseInt(request.getParameter("perm_user"));
                    if (user.isAdmin() || user.getId() == perm_user2) {
                        f.validerInvite(perm_topic2, perm_user2);
                        DisplayMonCompte(user, request, response);
                    }else{
                        DisplayErrorPage("Vous n'avez pas les droit pour cela.", "Public?op=bienvenue", request, response);
                    }
                    break;
                default:
                    DisplayErrorPage("Requete non reconnue par Public", "", request, response);
            }
        }else {
            DisplayTopicList(user, request,response);
        }

    }

    public void DisplayMonCompte(Utilisateur user, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("sessionUser", user);
        request.setAttribute("permitedTopics", f.getPermitedTopics(user));
        request.setAttribute("invitedTopics", f.getInvitedTopics(user));
        request.getRequestDispatcher("mon_compte.jsp").forward(request, response);
    }
}
