package br.edu.ufcg.leda.sender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Sender {

	protected File arquivo;
	protected String id;
	protected String url;

	public abstract void send() throws ClientProtocolException, IOException;

	void writeTicket(String logFileName, String content) throws IOException {

		File file = new File(logFileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fr = new FileWriter(file);
		fr.write(content);
		fr.close();
	}
}