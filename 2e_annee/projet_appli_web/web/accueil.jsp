<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Collection, Entities.*" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">

	<%List<Topic> topics = (List<Topic>) request.getAttribute("ListeTopics");
	Utilisateur user = (Utilisateur) session.getAttribute("user");
    List<Collection<Label>> topicsLabels = (List<Collection<Label>>) request.getAttribute("topicsLabels");
    boolean isAdmin = (boolean)request.getAttribute("isAdmin");
    %>
    <head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Accueil</title>

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

    <!-- Page Content -->
    <div class="container">
        <div id="newtopic" class="row">
            <div class="col-md-3">
            <%if (isAdmin) {%>
            <form id="signup" method="post" action="Admin">
                <h1></h1>
                <input type="submit" value="Administrer les Labels" class="inputButton"/>
                <input type="hidden" name="op" value="creationlabel">
            </form>
            <%}%>
            </div>
        </div>
    </div>
    <div class="container">
	<%
	if (topics != null) {
	    int i = 0;
	for (Topic t : topics) {%>
      <div id="topicbox" class="row vdivide">
		
		<div class="col-md-2">
			<a href="Restricted?op=afficherTopic&topicId=<%= t.getId() %>" ><img src="img/topic.png" class="rounded"></a>
			<a style="color:#f14444"> posts : <%=t.getNbMessages()%> </a>
		</div>
		
		<!-- topic -->
        <div class="col-md-8">
			<h1><%= t.getTitre()%></h1>
            <h2>Labels: <%for (Label l : topicsLabels.get(i)){%>
               <%=l.getText()+", "%>
            <%}%></h2>
			<hr/>
            <h3><% if(t.getNbMessages() > 0) {%> <%= t.getMessages().get(0).getContenu()%> <%}%></h3>
            <a href="Restricted?op=afficherTopic&topicId=<%= t.getId() %>" class="btn btn-primary">Rejoindre! &rarr;</a>
        </div>
		
		<!-- auteur -->
        <div class="col-md-2">
			<img src="img/user.jpg" class="rounded" style="width:100%">
			<a href="#" style="color:#f14444"><i class="fa fa-user"></i> <%= t.getCreateur().getPseudo()%>  </a><br/>
            <a href="#" style="color:#f14444"><i class="fa fa-calendar"></i> 01/01/18 </a>
			<a href="#" style="color:#f14444"><i class="fa fa-clock-o"></i> 17:57 </a>
        </div>
      </div>
	  <%i++;
	    }
		}%>
      <!-- /.row -->

    </div>

    <!-- /.container -->
	
	<div id="bouttonCreation" class="fixed-bottom">
		<a href="Restricted?op=creationtopic" class="btn  btn-primary btn-circle" style="background-color:#f14444"><i class="fa fa-plus"></i></a>
	</div>

    <!-- Footer -->
    <div ng-include="'modules/footer.html'"></div>
    
    <!-- Bootstrap core JavaScript -->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

  </body>

</html>
