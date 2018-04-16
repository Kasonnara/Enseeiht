<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Collection, Entities.*" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
  <%
	Topic t = (Topic)request.getAttribute("topic");
	List<Message> messages = t.getMessages();
	int userID = (int) session.getAttribute("sessionUserID");
  %>
  <head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Topic : <%= t.getTitre()%></title>

    <!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	
	<!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="vendor/simple-line-icons/css/simple-line-icons.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:300,400,700,300italic,400italic,700italic" rel="stylesheet" type="text/css">

    <!-- Custom styles for this template -->
    <link href="css/topic.css" rel="stylesheet">
	
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>

  </head>

   <body ng-app>

    <!-- Navigation -->
    <nav class="navbar navbar-default navbar-fixed-top topnav" role="navigation">
        <div ng-include="'modules/top_navbar.jsp'"></div>
    </nav>
	
	<%
		if (messages != null) {
			for (Message m : messages) {%>
		<div id="messagebox" class="row vdivide">
		
		
		<!-- message -->
        <div class="col-md-10">
			<div class="text-left" style="max-height:10%;color:#313538">#<%= m.getNumero()%> <% if (m.getNumero() == 0) {%> <%=t.getTitre()%> <%}%></div>
			<hr/>
            <div class="text-left" style="color : #313538"><%=m.getContenu()%></div>
        </div>
		
		<!-- auteur -->
        <div class="col-md-2">
			<img src="img/user.jpg" class="rounded" style="width:100%">
			<a href="#" style="color:#f14444"><i class="fa fa-user"></i> <%= m.getAuteur().getPseudo()%>  </a><br/>
            <a style="color:#f14444"><i class="fa fa-calendar"></i> <%=m.getJour()+"/"+m.getMois()+"/"+m.getAn()%></a>
        </div>
      </div>
	<%
			}
		}%>
	  
	  <b>Ecrire un commentaire :</b><br>
		<form method="post" action="Restricted">
		<p><input type="text" name="contenu"></p>
		<p><input type="submit" value="VALIDER"></p>
		<p><input type="hidden" name="topicId" value=<%=t.getId()%>></p>
		<p><input type="hidden" name="op" value="Vcommentaire"></p>
		</form>
		<br>
		<%if ((boolean)request.getAttribute("canInvite")) {%>
			Personnes ayant des permissions sur ce topic:<br>

			<%List<Utilisateur> permittedUsers = (List<Utilisateur>)request.getAttribute("permittedUsers");
			for (Utilisateur u : permittedUsers) {%>
				- <%= u.getPseudo()%> <br>
			<%}%>
			<br>
			Invitation en attentes:<br>
			<% List<Utilisateur> invitedUsers = (List<Utilisateur>)request.getAttribute("invitedUsers");
			if (invitedUsers.size() == 0) out.println("Personne");
			else {
			for (Utilisateur u : invitedUsers) {%>
				- <%= u.getPseudo()%> <br>
			<%}}%>
			<form method="post" action="Restricted?op=Vinvite">
				<input type="text" name="invitePseudo" placeholder="pseudo">
				<input type="submit" value="Envoyer une invitation">
				<input type="hidden" name="topicId" value=<%=t.getId()%>>
				<input type="hidden" name="op" value="invite">
			</form>
			<%String inviteErrorMessage = (String)request.getAttribute("InviteError");
				if (inviteErrorMessage!=null){%>
		<p style="color:red"><%=inviteErrorMessage%></p>
				<%}%>
		<%}%>
		
		<!-- Footer -->
    <div ng-include="'modules/footer.html'"></div>
    
    <!-- Bootstrap core JavaScript -->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
	
	
   </body>
</html>
