package green.atm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public enum ModalidadeTransacao {
	RH_ORGANICO_SEGURANCA, RH_EXTERNO, TERCEIRIZADO, ENCARGO, AGUA, 
	ENERGIA, PGTO_BOLETO, TAXA_BANCARIA, RECOLHIMENTO_COND, INTEGRALIZACAO, 
	APLICACAO_RDC, CRED_DIST_SOBRAS, CHEQUE_PAGO_CAIXA, FGTS, INSS, ISS, IRPF, OUTROS;
	
	public static void main(String[] args) throws IOException {
		Configuration config = Configuration.getInstance();
		List<String> func = config.getFuncionarios();
		HashMap<String,ModalidadeTransacao> mod = config.getModalidades();
		Gson gson = new Gson();
		File file = new File("/Users/adalbertocajueiro/Downloads/test.json");
		//File f = new File("src/main/resources/modalidades.json");
		File folder = new File("conf");
		File f2 = new File(folder,"modalidades.json");
		boolean existe = f2.exists();
		//BufferedReader br = new BufferedReader(new InputStreamReader(ModalidadeTransacao.class.getResourceAsStream("modalidades.json")));
		BufferedReader br = new BufferedReader(new FileReader(f2));
		HashMap<String,ModalidadeTransacao> result = new Gson().fromJson(br, HashMap.class);
		
		JsonWriter writer = new JsonWriter(new FileWriter(file));
		HashMap<String, ModalidadeTransacao> modalidades = new HashMap<String, ModalidadeTransacao> ();
		modalidades.put("DEB.EMI.TED DIF.TIT", ModalidadeTransacao.RH_EXTERNO);
		modalidades.put("DÉB.CONV.SANEAMENTO", ModalidadeTransacao.AGUA);
		modalidades.put("CRÉD.LIQ.COBRANÇA", ModalidadeTransacao.RECOLHIMENTO_COND);
		modalidades.put("DEB.TR.CT.DIF.TIT.", ModalidadeTransacao.RH_ORGANICO_SEGURANCA);
		modalidades.put("DEB PACOTE TARIFAS", ModalidadeTransacao.TAXA_BANCARIA);
		modalidades.put("DÉB.TIT.COMPE.EFETI", ModalidadeTransacao.PGTO_BOLETO);
		modalidades.put("CH COOP/AG.DEP.CTA", ModalidadeTransacao.TERCEIRIZADO);
		modalidades.put("DEB.PARC.SUBS/INTEG", ModalidadeTransacao.INTEGRALIZACAO);
		modalidades.put("DÉB. TIT. COBRANÇA", ModalidadeTransacao.PGTO_BOLETO);
		modalidades.put("APLICAÇÃO RDC", ModalidadeTransacao.APLICACAO_RDC);
		modalidades.put("CRÉD.DIST.SOBRAS", ModalidadeTransacao.CRED_DIST_SOBRAS);
		modalidades.put("TARIFA COBRANÇA", ModalidadeTransacao.TAXA_BANCARIA);
		modalidades.put("CHEQUE PAGO CAIXA", ModalidadeTransacao.CHEQUE_PAGO_CAIXA);
		modalidades.put("DÉB.TRANSF.POU.INTE", ModalidadeTransacao.OUTROS);
		modalidades.put("DÉB CONV. FGTS", ModalidadeTransacao.FGTS);
		modalidades.put("CHQ.DEV.MOT.48", ModalidadeTransacao.OUTROS);
		modalidades.put("CHQ CMP INTEGRADA", ModalidadeTransacao.OUTROS);
		modalidades.put("DEB.PGTO AG.GPS", ModalidadeTransacao.INSS);
		modalidades.put("DB.CONV.TR FD-RFB", ModalidadeTransacao.IRPF);
		modalidades.put("DÉB.TIT.COB.EFETIV", ModalidadeTransacao.OUTROS);

		
		System.out.println(gson.toJson(modalidades));
	}
}
