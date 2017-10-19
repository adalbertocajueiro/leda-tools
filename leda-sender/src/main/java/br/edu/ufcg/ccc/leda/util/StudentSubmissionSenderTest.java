package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

public class StudentSubmissionSenderTest {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//Inet4Address.getLocalHost()
		
		String url = "http://localhost/submit3";
		String semestre = "2016.1";
		String turma = "01";
		File arquivo = new File("D:\\trash\\115110563.zip");
		String matricula = "115110568";
		String roteiro = "R01";

		Sender sender = new StudentSubmissionSender(arquivo, matricula,
				semestre, roteiro, url, new HashMap<String,String>());
		sender.send();

	}

}
