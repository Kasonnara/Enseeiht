 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Collection, Entities.*" %>
<%@ page import="java.util.List" %>
 <head>
	<link rel="stylesheet" href="css/top_navbar_restricted.css" />
	
	<!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="vendor/simple-line-icons/css/simple-line-icons.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:300,400,700,300italic,400italic,700italic" rel="stylesheet" type="text/css">

	
 </head>
 <% Utilisateur u = (Utilisateur) session.getAttribute("user");	%>
 <nav class="navbar navbar-findcond fixed-top bg-faded" role="navigation">
    <div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="Restricted?op=accueil">Forum7</a>
		</div>
		<form class="navbar-form navbar-right search-form" role="search">
				<input type="text" class="form-control" placeholder="Recherche..." />
		</form>
		<ul class="nav navbar-right">
			<li class="dropdown">
				<%List<Invitation> invitations = u.getInvitations();%>
				<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><i class="fa fa-fw fa-bell-o"></i> Notifications <span class="badge" style="color:#fff"><%=invitations.size()%></span></a>
				<ul class="dropdown-menu" role="menu">
					<%if (invitations.size() != 0) {
						for (Invitation i: invitations) {%>
					<li><a href="Restricted?op=Vaccepter_invitation&perm_topic=<%= i.getTopic().getId()%>&perm_user=<%=u.getId()%>"><i class="fa fa-fw fa-tag"></i> <span class="badge">Invitation</span> <%= i.getTopic().getTitre()%> </a></li>
						<% }
					}%>
				</ul>
				
			</li>
			<li class="dropdown">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><%=u.getPseudo()%> <span class="caret"></span></a>
				<ul class="dropdown-menu" role="menu">
					<li><a href="Restricted?op=moncompte">Mon Compte</a></li>
					<li><a href="Restricted?op=devenirVIP">Devenir VIP</a></li>
					<li class="divider"></li>
					<li><a href="Restricted?op=deconnexion">Déconnexion</a></li>
				</ul>
			</li>
		</ul>
	</div>
</nav>
