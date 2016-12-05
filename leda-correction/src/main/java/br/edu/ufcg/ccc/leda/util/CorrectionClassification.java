package br.edu.ufcg.ccc.leda.util;

import java.util.Arrays;

public enum CorrectionClassification {
	EXCELENTE("Excelente"),BOM("Bom"),REGULAR("Regular"),
		RUIM("Ruim"),PRESENCA("Presença"),FALTA("Falta");

	private String classification;

	CorrectionClassification(String classification) {
		this.classification = classification;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(CorrectionClassification.values()));
		CorrectionClassification c = Enum.valueOf(CorrectionClassification.class,"BOM");
		System.out.println(c);
		int i = 0;
	}
}
