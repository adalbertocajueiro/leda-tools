package br.edu.ufcg.ccc.leda.util;

import java.util.ArrayList;

public class CorrectionReport {
	private String id;
	private String matriculaCorretor;
	private ArrayList<CorrectionReportItem> reportItems;
	
	public CorrectionReport(String id, String matriculaCorretor, ArrayList<CorrectionReportItem> reportItems) {
		super();
		this.id = id;
		this.matriculaCorretor = matriculaCorretor;
		this.reportItems = reportItems;
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
	
	
	
}
