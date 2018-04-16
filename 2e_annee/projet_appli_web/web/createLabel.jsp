<%@ page import="Entities.Label" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Label</title>


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


<!---
<form method="get" action="Restricted">
    <p><input type="submit" value="Déconnexion"></p>
    <p><input type="hidden" name="op" value="deconnexion"></p>
</form>
<form method="get" action="Restricted">
    <p><input type="submit" value="< page Accueil"></p>
    <p><input type="hidden" name="op" value="accueil"></p>
</form>
<form method="get" action="Restricted">
    <p><input type="submit" value="Mon compte"></p>
    <p><input type="hidden" name="op" value="moncompte"></p>
</form><br>
-->
<b>LISTE DES LABELS</b>
<% List<Label> labels = (List<Label>)request.getAttribute("labels");
for (Label l : labels){%>
    <p style="color:<%=l.getHtml_color()%>"><%=l.getText()%></p>
<%}%>
<br>

<b>CREER UN LABEL :</b><br>

<div class="container"></div><section class="container">
    <div class="row">
        <div class="col-md-10">
            <form class="form-horizontal" method="post" action="Admin">
                <div class="form-group">
                    <label for="texte" class="control-label col-xs-2">Texte : </label>
                    <div class="col-xs-10">
                        <input class="form-control" name="texte" placeholder="Entrez un titre" type="text">
                    </div>
                </div>
                <div class="form-group">
                    <label for="couleur" class="control-label col-xs-2">Couleur : </label>
                    <div class="col-xs-10">
                        <input class="form-control" name="color" placeholder="#FF00FF" type="text">
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-xs-offset-2 col-xs-10">
                        <button type="submit" class="btn btn-primary">CREER</button>
                        <input type="hidden" name="op" value="Vcreationlabel">
                    </div>
                </div>

            </form>
        </div>
    </div>
</section>


<!--
<form method="post" action="Admin">
    <p>Texte : <input type="text" name="texte"></p>
    <p>Couleur : <input type="text" name="color" placeholder="#FF00FF"></p>
    <p><input type="submit" value="CREER"></p>
    <p><input type="hidden" name="op" value="Vcreationlabel"></p>
</form>
-->


<!-- Footer -->
<div ng-include="'modules/footer.html'"></div>

<!-- Bootstrap core JavaScript -->
<script src="vendor/jquery/jquery.min.js"></script>
<script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>


</body>
</html>