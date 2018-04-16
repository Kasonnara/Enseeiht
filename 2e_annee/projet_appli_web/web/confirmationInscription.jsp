
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
L'utilisateur a bien été enregistré
<p>prenom :<%=request.getAttribute("prenom") %></p>
<p>nom :<%=request.getAttribute("nom") %></p>
<p>pseudo :<%=request.getAttribute("pseudo") %></p>
<p>mot de passe :<%=request.getAttribute("mdp") %></p>
<meta http-equiv="refresh" content="3; Public?op=connexion">

</body>
</html>
