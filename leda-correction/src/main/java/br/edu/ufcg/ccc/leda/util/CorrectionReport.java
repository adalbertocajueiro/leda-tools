package br.edu.ufcg.ccc.leda.util;

import java.util.ArrayList;

public class CorrectionReport {
	private String id;
	private String matriculaCorretor;
	private int numberOfTests;
	private ArrayList<CorrectionReportItem> reportItems; 
	
	public CorrectionReport() {
		super();
		this.reportItems = new ArrayList<CorrectionReportItem>();
	}

	public CorrectionReport(String id, String matriculaCorretor, 
			int numberOfTests, ArrayList<CorrectionReportItem> reportItems) {
		super();
		this.id = id;
		this.matriculaCorretor = matriculaCorretor;
		this.numberOfTests = numberOfTests;
		this.reportItems = reportItems;
	}
	
	public CorrectionReportItem getCorrectionReportItemforStudent(String matricula){
		CorrectionReportItem result = null;
		result = this.reportItems.stream().filter(cri -> cri.getMatricula().equals(matricula)).findFirst().orElse(null);
		return result;
	}

	public void setComentario(String matriculaAluno, String comentario){
		CorrectionReportItem reportItem =  reportItems.stream().filter(item -> item.getMatricula().equals(matriculaAluno)).findFirst().orElse(null);
		if(reportItem == null){
			throw new CorrectionReportException("Nao pode adicionar comentario ao aluno " + matriculaAluno + " para atividade " + id);
		}
		reportItem.setComentario(comentario);
	}
	public void setNotaDesign(String matriculaAluno, double notaDesign){
		CorrectionReportItem reportItem =  reportItems.stream().filter(item -> item.getMatricula().equals(matriculaAluno)).findFirst().orElse(null);
		if(reportItem == null){
			throw new CorrectionReportException("Nao pode adicionar nota de design ao aluno " + matriculaAluno + " para atividade " + id);
		}
		reportItem.setNotaDesign(notaDesign);
	}
	public void setClassificacao(String matriculaAluno, CorrectionClassification classificacao){
		CorrectionReportItem reportItem =  reportItems.stream().filter(item -> item.getMatricula().equals(matriculaAluno)).findFirst().orElse(null);
		if(reportItem == null){
			throw new CorrectionReportException("Nao pode adicionar classificacao ao aluno " + matriculaAluno + " para atividade " + id);
		}
		reportItem.setClassification(classificacao);
	}
	
	public void setAdequacao(String matriculaAluno, CodeAdequacy adequacy){
		CorrectionReportItem reportItem =  reportItems.stream().filter(item -> item.getMatricula().equals(matriculaAluno)).findFirst().orElse(null);
		if(reportItem == null){
			throw new CorrectionReportException("Nao pode adicionar adequacao ao aluno " + matriculaAluno + " para atividade " + id);
		}
		reportItem.setAdequacy(adequacy);
	}
	
	public String getComentario(String matriculaAluno){
		CorrectionReportItem reportItem =  reportItems.stream().filter(item -> item.getMatricula().equals(matriculaAluno)).findFirst().orElse(null);
		if(reportItem == null){
			throw new CorrectionReportException("Nao pode localizar comentario do aluno " + matriculaAluno + " para atividade " + id);
		}
		
		return reportItem.getComentario();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMatriculaCorretor() {
		return matriculaCorretor;
	}

	public void setMatriculaCorretor(String matriculaCorretor) {
		this.matriculaCorretor = matriculaCorretor;
	}

	public ArrayList<CorrectionReportItem> getReportItems() {
		return reportItems;
	}

	public void setReportItems(ArrayList<CorrectionReportItem> reportItems) {
		this.reportItems = reportItems;
	}

	public int getNumberOfTests() {
		return numberOfTests;
	}

	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
	}
	
	
	
}
