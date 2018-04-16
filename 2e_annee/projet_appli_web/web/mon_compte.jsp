<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Collection, facade.*, Entities.*" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Mon Compte</title>

    <!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	
	<!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="vendor/simple-line-icons/css/simple-line-icons.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:300,400,700,300italic,400italic,700italic" rel="stylesheet" type="text/css">

    <!-- Custom styles for this template -->
    <link href="css/accueil.css" rel="stylesheet">
	
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>

  </head>

   <body ng-app>

    <!-- Navigation -->
    <nav class="navbar navbar-default navbar-fixed-top topnav" role="navigation">
        <div ng-include="'modules/top_navbar.jsp'"></div>
    </nav>
	<%
	Utilisateur p = (Utilisateur) request.getAttribute("sessionUser");
	String VIP;
	if (p.isVIP()) {
		VIP = "oui";
	} else {
		VIP = "non";
	}%>
		Mon nom : <i><%= p.getNom() %></i><br>
		Mon prénom : <i><%= p.getPrenom() %></i><br>
		Mon pseudonyme : <i><%= p.getPseudo() %></i><br>
		Mon mot de passe : <i><%= p.getMdp() %></i><br>
		VIP ? : <i><%= VIP %></i><br><br>
		<br>
		<a href = "Restricted?op=devenirVIP">Devenir VIP</a>
		<br>
		Permissions: <br>
		<%
		List<Topic> permitedTopics = (List<Topic>)request.getAttribute("permitedTopics");
			if (permitedTopics.size() == 0) out.println("aucune");
			else {
				for (Topic t : permitedTopics) {%>

		<form method="get" action="Restricted" >
			- <%=t.getTitre()%> <input type="submit" value="Supprimer">
			<input type="hidden" name="op" value="Vdelete_permission">
			<input type="hidden" name="perm_topic" value=<%=t.getId()%>>
			<input type="hidden" name="perm_user" value=<%=p.getId()%>>
		</form>
		<%}}%>
		<br>
		Invitations: <br>
		<%List<Topic> invitedTopics = (List<Topic>)request.getAttribute("invitedTopics");
			if (invitedTopics.size() == 0) out.println("aucune");
			else {
				for (Topic t : invitedTopics) {%>
		<form method="get" action="Restricted" >
			- <%=t.getTitre()%> <input type="submit" value="Accepter">
			<input type="hidden" name="op" value="Vaccepter_invitation">
			<input type="hidden" name="perm_topic" value=<%=t.getId()%>>
			<input type="hidden" name="perm_user" value=<%=p.getId()%>>
		</form>
		<form method="get" action="Restricted" >
			<input type="submit" value="Supprimer">
			<input type="hidden" name="op" value="Vdelete_permission">
			<input type="hidden" name="perm_topic" value=<%=t.getId()%>>
			<input type="hidden" name="perm_user" value=<%=p.getId()%>>
		</form>
		<%}}%>
	<!-- Footer -->
    <div ng-include="'modules/footer.html'"></div>
    
    <!-- Bootstrap core JavaScript -->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

  </body>

</html>
