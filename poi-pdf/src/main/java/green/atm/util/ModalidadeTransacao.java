package green.atm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public enum ModalidadeTransacao {
	RH_ORGANICO, RH_EXTERNO, SEGURANCA, TERCEIRIZADO, ENCARGO, AGUA, ENERGIA, FORNECEDOR, TAXA_BANCARIA, OUTROS;
	
	public static void main(String[] args) throws IOException {
		Gson gson = new Gson();
		File file = new File("/Users/adalbertocajueiro/Downloads/test.json");
		File f = new File("src/main/resources/modalidades.json");
		//BufferedReader br = new BufferedReader(new InputStreamReader(ModalidadeTransacao.class.getResourceAsStream("modalidades.json")));
		BufferedReader br = new BufferedReader(new FileReader(f));
		HashMap<String,ModalidadeTransacao> result = new Gson().fromJson(br, HashMap.class);
		
		JsonWriter writer = new JsonWriter(new FileWriter(file));
		HashMap<String, ModalidadeTransacao> modalidades = new HashMap<String, ModalidadeTransacao> ();
		modalidades.put("DEB.TR.CT.DIF.TIT.", ModalidadeTransacao.RH_ORGANICO);
		modalidades.put("DEB PACOTE TARIFAS", ModalidadeTransacao.TAXA_BANCARIA);
		modalidades.put("DÉB.CONV.SANEAMENTO", ModalidadeTransacao.AGUA);
		System.out.println(gson.toJson(modalidades));
	}
}
