package servlet;

import Entities.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servelet regroupant toutes les action réalisable sans se connecter
 */

@WebServlet("/Public")
public class PublicServlet extends Serv{

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String op = request.getParameter("op");
        if(op != null) {
            switch (op) {
                case "bienvenue":
                    request.getRequestDispatcher("bienvenue.html").forward(request, response);
                    break;
                case "inscription":
                    request.getRequestDispatcher("inscription.html").forward(request, response);
                    break;
                case "connexion":
                    request.getRequestDispatcher("connexion.html").forward(request, response);
                    break;
                case "accueil":
                    request.getRequestDispatcher("accueil.html").forward(request, response);
                    break;
                case "Vinscription":
                    String nom = request.getParameter("nom");
                    String prenom = request.getParameter("prenom");
                    String pseudo = request.getParameter("pseudo");
                    String mdp = request.getParameter("mdp");
                    Utilisateur u_cree = f.ajoutUtilisateurAndReturn(nom, prenom, pseudo, mdp);
                    if (u_cree != null) {
                        request.setAttribute("nom", u_cree.getNom());
                        request.setAttribute("prenom", u_cree.getPrenom());
                        request.setAttribute("pseudo", u_cree.getPseudo());
                        request.setAttribute("mdp", u_cree.getMdp());
                        request.getRequestDispatcher("confirmationInscription.jsp").forward(request, response);
                    }else {
                        DisplayErrorPage("L'utilisateur existe déjà", "Public?op=inscription", request, response);
                    }
                    break;
                case "Vconnexion":
                    String pseudo2 = request.getParameter("pseudo");
                    String mdp2 = request.getParameter("mdp");
                    Utilisateur u = f.rechercherUtilisateur(pseudo2);
                    if (u == null) {
                        // le compte n'existe pas
                        String s = "";
                        for (Utilisateur a_user : f.listerUtilisateurs()) {
                            s = s + a_user.getPseudo() + "\n"; // POUR LE DEGUB UNIQUEMENT TODO supprimer en prod
                        }
                        DisplayErrorPage("Le compte n'existe pas\nliste des compte :\n" + s, "Public?op=bienvenue", request, response);
                    } else if (mdp2.equals(u.getMdp())) {
                        // connexion OK
                        HttpSession session = request.getSession();
                        session.setAttribute("sessionUserID", u.getId());
                        session.setAttribute("user", u);
                        session.setAttribute("isAdmin", u.isAdmin()?true:null);
                        session.setMaxInactiveInterval(30 * 60);
                        DisplayTopicList(u, request, response);
                    } else {
                        // Mauvais mot de passe
                        // affichage du vrai mdp : POUR LE DEGUB UNIQUEMENT TODO supprimer en prod
                        DisplayErrorPage("mauvais mot de passe, bon mot de passe :'" + u.getMdp() + "'", "Public?op=connexion", request, response);
                    }
                    break;
                default:
                    DisplayErrorPage("Requete non reconnue par Public", "", request, response);
            }
        }else{
            request.getRequestDispatcher("").forward(request, response);
        }

    }
}
