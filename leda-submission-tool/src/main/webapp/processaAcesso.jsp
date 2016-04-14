<%@ page import="upload.util.*, java.util.*" %>

<%
String IP = request.getParameter("originalIP");
System.out.println("Access from " + IP + " in " + (new Date()).toString());
%>

<html>
<head>
<title>Sistema de submissão de provas práticas - LEDA</title>
</head>
<body>
<h3>Antes de baixar, verifique se seu nome aparece na lista dos alunos para enviar! </h3><br>

<!--      
<a href="1oEstagioRep-environment.zip">Baixe aqui</a> os arquivos da reposicao do 1o estagio .<br>
<a href="2oEstagioRep-environment.zip">Baixe aqui</a> os arquivos da reposicao do 2o estagio .<br>
<a href="3oEstagioRep-environment.zip">Baixe aqui</a> os arquivos da reposicao do 3o estagio .<br>

 --> 
  
Acesso de <% out.print(IP); %>
</body>
</html>