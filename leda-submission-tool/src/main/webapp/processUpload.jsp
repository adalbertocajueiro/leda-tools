<%@ page import="com.oreilly.servlet.multipart.*, com.oreilly.servlet.*, java.io.*,java.util.*" %>
<%@ page import="upload.util.*" %>

<%
  //Utilities.load();
  String pathUploadFiles = Utilities.getFolderName();
  MultipartRequest multipartRequest;
  
  File diretorioUpload = new File(pathUploadFiles);
  //multipartRequest = new MultipartRequest(request, "C:\\temp",2097152);
  String homeDirectory = System.getProperty("user.home");
  multipartRequest = new MultipartRequest(request, homeDirectory,2097152);

  String campoArquivo;
  String nomeArquivo = "";
  File file;
  String nomeAluno;
  FileInputStream fileStream;
  byte[] bytes;

  //Enumeration arquivos = multipartRequest.getFileNames();
  nomeAluno = multipartRequest.getParameter("nomeAluno");
  //campoArquivo = (String) arquivos.nextElement();
  nomeArquivo = multipartRequest.getFilesystemName("arquivo");
  
 
  if (nomeArquivo != null) {
    //out.println("Campo arquivo: " + campoArquivo + "<br>");
    //out.println("Descricao arquivo: " + descricao + "<br>");
    //out.println("Nome do arquivo: " + nomeArquivo + "<br>");

    //lendo os bytes do arquivo
    file = multipartRequest.getFile("arquivo");
    bytes = new byte[(int) file.length()];
    fileStream = new FileInputStream(file);
    fileStream.read(bytes);

    //leu e armazenou em um aray de bytes. Precisa agora abrir o arquivo de destino 
    //e mandar escreber o array de bytes nele e depois remover o nome do aluno da lista
    //de arquivos enviados.
    
    System.out.println("Folder: " + Utilities.getFolderName());
    System.out.println("Nome do aluno: " + nomeAluno);
    File output = new File(Utilities.getSubmissionsFolder().getAbsolutePath() + File.separator + nomeAluno + ".zip");
    FileOutputStream fos = new FileOutputStream(output);
    fos.write(bytes);
    fos.close();
    Utilities.removeName(nomeAluno);
    System.out.println("Tamanho do arquivo: " + bytes.length + " bytes");
    fileStream.close();
    file.delete();
    System.out.println("Hora do envio: " + new Date(System.currentTimeMillis()));
    
    //System.out.println("arquivo salvo com sucesso");
    System.out.println("Arquivos salvos: " + Utilities.getSalvos() + "(de um total de: " + Utilities.getQuantidade() + ")");
    //Insere os dados no BD
    //Fachada.insereDados(nomeArquivo,descricao, bytes);

    out.println("Arquivo enviado com sucesso! Feche a janela! Não volte e submeta novamente!");
  }

%>

