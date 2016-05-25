<%@ page import="upload.util.*, java.util.*" %>

<%
String IP = request.getRemoteAddr();
System.out.println(IP + " - " + (new Date()).toString());
  
  //if(!IP.startsWith("150.165.74") && !IP.startsWith("150.165.80")){
  //System.out.println(IP);
  if(!IP.startsWith("150.165.74")
	 && !IP.startsWith("150.165.75")
  	 && !IP.startsWith("0:0:0:0:0:0:0:1")
  	 && !IP.startsWith("127.0.0.1")
	 && !IP.startsWith("150.165.80.186")
    ){
	  //if(false){
%>
  <jsp:forward page="acessoNegado.jsp"/>
	
<%
  }
%>

<html>
<head>
<title>Sistema de submissão de provas práticas - LEDA</title>
</head>
<body>
<h3>Antes de baixar, verifique se seu nome aparece na lista dos alunos para enviar! </h3><br>

     
<a href="Prova-QuickSelect-Reposicao-environment.zip">Baixe aqui</a> os arquivos da reposicao do 1o estagio .<br>
<a href="Prova-ProcessScheduler-Reposicao-environment.zip">Baixe aqui</a> os arquivos da reposicao do 2o estagio .<br>
<a href="Prova-OneStepSplayTree-environment.zip">Baixe aqui</a> os arquivos da reposicao do 3o estagio .<br>

 
 <!--  
<a href="Prova-SkipListFromBST-environment.zip">Baixe os arquivos da prova.</a><br>

 -->
Escolha seu nome, seu arquivo contendo sua resposta e clique em enviar.<br>
<font color="#FF0000">Voce tem direito de enviar apenas uma vez. Portanto, teste seu arquivo antes, selecione corretamente seu arquivo e seu nome.</font><br>
<br><form method="POST" action="processUpload.jsp" enctype="multipart/form-data">  
<select name="nomeAluno">
 <% 
    Vector<String> nomes = Utilities.getNames(); 
    Iterator<String> it = nomes.iterator();
    while(it.hasNext()){
    	String nome = it.next();
    	out.println("<option value='" + nome + "'>"+ nome + "</option>");
    }
 %>
</select><BR>
 <input type="file" name="arquivo"><BR>  
 <input type="submit">  
</form>
</body>
</html>