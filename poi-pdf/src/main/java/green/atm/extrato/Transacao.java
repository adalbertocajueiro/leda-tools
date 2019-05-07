package green.atm.extrato;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Transacao {
	private GregorianCalendar data;
	private String textoIdentificador;
	private double valor;
	private String descricao;
	private String numeroDOC;
	public Transacao(GregorianCalendar data, String textoIdentificador, double valor, String descricao,
			String numeroDOC) {
		super();
		this.data = data;
		this.textoIdentificador = textoIdentificador;
		this.valor = valor;
		this.descricao = descricao;
		this.numeroDOC = numeroDOC;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY");
		
		return df.format(this.data.getTime()) + " - " + this.valor + " - " + this.numeroDOC;
	}

	public GregorianCalendar getData() {
		return data;
	}

	public void setData(GregorianCalendar data) {
		this.data = data;
	}

	public String getTextoIdentificador() {
		return textoIdentificador;
	}

	public void setTextoIdentificador(String textoIdentificador) {
		this.textoIdentificador = textoIdentificador;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNumeroDOC() {
		return numeroDOC;
	}

	public void setNumeroDOC(String numeroDOC) {
		this.numeroDOC = numeroDOC;
	}
	
	
}
