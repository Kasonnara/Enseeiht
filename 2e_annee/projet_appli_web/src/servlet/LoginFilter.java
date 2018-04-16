package servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebFilter("/*") 
public class LoginFilter implements Filter { 
	
    public  void init(FilterConfig filterConfig) {
        // l'objet filterConfig encapsule les paramètres 
        // d'initialisation de ce filtre
        
    }
    
     public  void destroy() {
        // callback de destruction de ce filtre
    }
 
    @Override 
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {    
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String adminURI = request.getContextPath() + "/Admin";
        String restrictedURI = request.getContextPath() + "/Restricted";

        String uri = request.getRequestURI();

        boolean connectedAdmin = session != null && session.getAttribute("isAdmin") != null ;
        boolean connectedUser = connectedAdmin || (session != null && session.getAttribute("sessionUserID") != null);

        boolean restrictedPage = uri.startsWith(restrictedURI);
        boolean adminPage = uri.startsWith(adminURI);
        boolean publicPage = uri.startsWith(request.getContextPath() + "/Public") || uri.equals(request.getContextPath()+"/");

        //Serv.DisplayErrorPage("Filter uri=" + uri + "<br> restrictedTemplate ="+restrictedURI, "connexion.html", request, response);

        // Autorise l'accès aux plugins, css, js, template, etc reste accessible "
        if ( uri.indexOf("/vendor") > 0 || uri.indexOf("/modules") > 0 || uri.indexOf("/img") > 0 || uri.indexOf("/css") > 0 || uri.indexOf("/scss") > 0 || uri.indexOf("/js") > 0 || uri.indexOf("/template") > 0 || uri.indexOf("/bootstrap") > 0){
            chain.doFilter(request, response);

        }else if ( publicPage || (connectedUser && restrictedPage) || (connectedAdmin && adminPage)  ) {
            // OK on poursuit la requete normalement
            chain.doFilter(request, response);
        } else {
            // Erreur connexion requise
            Serv.DisplayErrorPage("Vous devez etre connecte pour realiser cette action.", "Public?op=connexion", request, response);
        }

    }


} 