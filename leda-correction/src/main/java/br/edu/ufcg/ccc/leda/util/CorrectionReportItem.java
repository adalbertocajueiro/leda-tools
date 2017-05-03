package br.edu.ufcg.ccc.leda.util;

public class CorrectionReportItem {
	private String matricula;
	private String comentario = "";
	private CorrectionClassification classification;
	private CodeAdequacy adequacy;
	private TestReportItem testReportItem;
	private double notaDesign;
	
	public CorrectionReportItem(String matricula, String comentario, 
			CorrectionClassification classification, CodeAdequacy adequacy,
			TestReportItem testReportItem) {
		super();
		this.matricula = matricula;
		this.comentario = comentario;
		this.testReportItem = testReportItem;
		this.classification = classification;
		this.adequacy = adequacy;
		this.notaDesign = calculateNotaDesignDefault(classification);
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

	public double calculateNotaTestes(){
		double nota = 0.0;
		
		int testesAConsiderar = this.testReportItem.getTotalTests() - this.testReportItem.getSkipped();
		nota = ((double)(testsPassed(true)/(double)testesAConsiderar))*10;
		return nota;
	}
	
	public double calculateNotaTestesSemAdequacao(){
		double nota = 0.0;
		
		int testesAConsiderar = this.testReportItem.getTotalTests() - this.testReportItem.getSkipped();
		nota = ((double)(testsPassed(false)/(double)testesAConsiderar))*10;
		return nota;
	}
	
	private int testsPassed(boolean considerarAdequacao){
		int passed = 0;
		if(testReportItem.hasSubmitted() && !testReportItem.hasCompilationError()){
			passed = this.testReportItem.getTotalTests() - this.testReportItem.getErrors() - this.testReportItem.getFailures() - this.testReportItem.getSkipped();
			if(passed < 0){
				passed = 0;
			}else{
				//score = passed/(this.totalTests - this.skipped)*4.0;
				if(considerarAdequacao){
					switch (adequacy) {
					case BAIXA:
						passed = 0;
						break;
					case MEDIA:
						passed = passed - passed/2;
						break;
					case ALTA:
						break;
					}
				}
			}

		}
		return passed;
	}
	
	public double calcularNotaFinal(double pesoTestes, double pesoDesign){
		double nota = 0.0;
		nota = calculateNotaTestes()*pesoTestes + notaDesign*pesoDesign;
		
		return nota;
	}
	
	public double getNotaTestes(){
		return calculateNotaTestes();
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

	public CodeAdequacy getAdequacy() {
		return adequacy;
	}

	public void setAdequacy(CodeAdequacy adequacy) {
		this.adequacy = adequacy;
	}
	
	
	
}
