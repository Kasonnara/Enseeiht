package servlet;


import Entities.Label;
import Entities.Topic;
import Entities.Utilisateur;
import facade.Facade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Serv
 */
@WebServlet("/Serv")
public class Serv extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    protected Facade f;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Serv() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DisplayErrorPage("Requete non reconnue par Serv", "", request, response);
    }

    protected void DisplayTopicList(Utilisateur user, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /** envoi la page accueil.jsp
         */
        List<Topic> topics2 = f.ListerTopics(user);
        List<Collection<Label>> topicsLabels = new ArrayList<>();
        for (Topic t3 : topics2){
            topicsLabels.add(f.listerLabel(t3));
        }
        request.setAttribute("isAdmin", user.isAdmin());
        request.setAttribute("topicsLabels", topicsLabels);
        request.setAttribute("ListeTopics", topics2);
        request.getRequestDispatcher("accueil.jsp").forward(request, response);
    }
    protected void DisplayTopic(int topicId, Utilisateur user, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /** envoi une page topic.jsp
         */
        Topic t = f.getTopic(topicId);

        if (t==null){
            // Topic innexistant
            request.setAttribute("ListeTopics", f.ListerTopics());
            request.getRequestDispatcher("accueil.jsp").forward(request, response);
        }else {
            request.setAttribute("canInvite", t.canInvite(user,f));
            request.setAttribute("topic", t);
            if (t.canInvite(user,f)) {
                // Get les permission qui son lazy!
                List<Utilisateur> invitedUsers = f.getInvitedUser(t);
                List<Utilisateur> permittedUsers = f.getPermtedUsers(t);
                request.setAttribute("invitedUsers", invitedUsers);
                request.setAttribute("permittedUsers" , permittedUsers);
                request.getRequestDispatcher("topic.jsp").forward(request, response);
            }else {
                request.getRequestDispatcher("topic.jsp").forward(request, response);
            }
        }
    }

    public static void DisplayErrorPage(String message, String redirection, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * Envoi une page d'erreur
         */
        request.setAttribute("ErrorMessage", message);
        request.setAttribute("Redirection", redirection);
        request.getRequestDispatcher("errorPage.jsp").forward(request, response);
    }
}