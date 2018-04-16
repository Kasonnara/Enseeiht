 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Collection, Entities.*" %>
<%@ page import="java.util.List" %>
 
 <head>
	<link rel="stylesheet" href="css/top_navbar.css" />
	
	<!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="vendor/simple-line-icons/css/simple-line-icons.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:300,400,700,300italic,400italic,700italic" rel="stylesheet" type="text/css">

	<!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
	
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>
	
 </head>
 
 <% Utilisateur user = (Utilisateur) session.getAttribute("user");%>

 <% if(user == null) {%>
 <nav class="navbar navbar-findcond fixed-top bg-faded" role="navigation">
    <div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="Public?op=bienvenue">Forum7</a>
		</div>
		<ul class="nav navbar-right">
			<li class="dropdown">
				<a href="Public?op=connexion" class="dropdown-toggle" data-toggle="dropdown"><b>Se connecter</b> <span class="caret"></span></a>
				<ul id="login-dp" class="dropdown-menu" style="right: 0; left: auto;">
					<li>
					<div class="row">
						<div class="col-md-12">
							Se connecter avec
							<div class="social-buttons">
								<a href="#" class="btn btn-fb"><i class="fa fa-facebook"></i> Facebook</a>
								<a href="#" class="btn btn-tw"><i class="fa fa-twitter"></i> Twitter</a>
							</div>
							ou
							<form class="form" role="form" method="post" action="Public" accept-charset="UTF-8" id="login-nav">
								<div class="form-group">
									 <label class="sr-only" for="pseudo">Pseudo</label>
									 <input type="text" name="pseudo" class="form-control" id="pseudo" placeholder="Pseudo" required>
								</div>
								<div class="form-group">
									 <label class="sr-only" for="mdp">Mot de passe</label>
									 <input type="password" name="mdp" class="form-control" id="mdp" placeholder="Mot de Passe" required>
                                     <div class="help-block text-right"><a href="">Mot de passe oublié ?</a></div>
								</div>
								<div class="form-group">
									 <button type="submit" class="btn btn-primary btn-block">Se Connecter</button>
									 <input type="hidden" name="op" value="Vconnexion"></p>
								</div>
								<div class="checkbox">
									 <label>
									 <input type="checkbox"> se souvenir de moi
									 </label>
								</div>
							</form>
						</div>
						<div class="bottom text-center">
							Nouveau ? <a href="Public?op=inscription"><b>Rejoins-nous</b></a>
						</div>
					</div>
					</li>
				</ul>
			</li>
			<!-- <form class="navbar-form navbar-right search-form" role="search">
				<input type="text" class="form-control" placeholder="Search" />
			</form> -->
		</ul>
	</div>
</nav>

<% } else {%>
<body ng-app>
	<div ng-include="'modules/top_navbar_restricted.jsp'"></div>
</body>
<%}%>
