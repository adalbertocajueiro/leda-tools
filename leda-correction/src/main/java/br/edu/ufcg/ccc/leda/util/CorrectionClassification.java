package br.edu.ufcg.ccc.leda.util;

public enum CorrectionClassification {
	EXCELENTE("Excelente"),BOM("Bom"),REGULAR("Regular"),
		RUIM("Ruim"),PRESENCA("Presença"),FALTA("Falta");

	private String classification;

	private CorrectionClassification(String classification) {
		this.classification = classification;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
	
}
