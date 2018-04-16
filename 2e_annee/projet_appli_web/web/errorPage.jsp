<%--
  Created by IntelliJ IDEA.
  User: kasonnara
  Date: 10/01/18
  Time: 22:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    Erreur
    <p><%=request.getAttribute("ErrorMessage") %></p>
    <meta http-equiv="refresh" content="3; <%=request.getAttribute("Redirection") %>">
</body>
</html>
