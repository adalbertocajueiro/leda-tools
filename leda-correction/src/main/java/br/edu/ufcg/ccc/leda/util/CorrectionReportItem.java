package br.edu.ufcg.ccc.leda.util;

public class CorrectionReportItem {
	private String matricula;
	private String comentario = "";
	private CorrectionClassification classification;
	private double notaDesign;
	private double notaTestes;
	
	public CorrectionReportItem(String matricula, String comentario, CorrectionClassification classification,
			double notaDesign, double notaTestes) {
		super();
		this.matricula = matricula;
		this.comentario = comentario;
		this.classification = classification;
		this.notaDesign = calculateNotaDesignDefault(classification);
		this.notaTestes = notaTestes;
	}

	private double calculateNotaDesignDefault(CorrectionClassification classification) {
		double nota = 0;
		switch (classification) {
		case EXCELENTE:
			nota = 9.5;
			break;
		case BOM:
			nota = 8.0;
			break;
		case REGULAR:
			nota = 6.0;
			break;
		case RUIM:
			nota = 4.0;
			break;
		default:
			break;
		}
		return nota;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public CorrectionClassification getClassification() {
		return classification;
	}

	public void setClassification(CorrectionClassification classification) {
		this.classification = classification;
	}

	public double getNotaDesign() {
		return notaDesign;
	}

	public void setNotaDesign(double notaDesign) {
		this.notaDesign = notaDesign;
	}

	public double getNotaTestes() {
		return notaTestes;
	}

	public void setNotaTestes(double notaTestes) {
		this.notaTestes = notaTestes;
	}
	
	
	
}
