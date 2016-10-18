package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import com.google.gdata.util.ServiceException;

public class Submission {
	
	private Atividade atividade;
	private Student aluno;
	private GregorianCalendar data;
	private boolean fezDownload;
	private File arquivoSubmetido;
	
	public Submission(){
		this.fezDownload = true;
	}
	
	public Submission(String idAtividade, String matricula) throws ConfigurationException, IOException, ServiceException, AtividadeException, StudentException {
		this.atividade = buscarAtividade(idAtividade);
		this.aluno = buscarAluno(matricula);
		this.fezDownload = buscarFezDownload(idAtividade,matricula);
		this.arquivoSubmetido = buscarSubmissao(idAtividade,matricula);
		if(this.arquivoSubmetido != null){
			this.data = new GregorianCalendar();
			this.data.setTimeInMillis(arquivoSubmetido.lastModified());
		}
	}

	private File buscarSubmissao(String idAtividade, String matricula) {
		File submissao = null;
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		File atividadeFolder = new File(currentSemester,idAtividade);
		File submissionsFolder = new File(atividadeFolder,Constants.SUBMISSIONS_FOLDER_NAME);
		List<File> files = Util.filesAsList(submissionsFolder);
		submissao = files.stream().filter(f -> f.getName().contains(matricula)).findAny().orElse(null);
		return submissao;
	}

	private boolean buscarFezDownload(String idAtividade, String matricula) throws IOException {
		boolean fezDownload = true;
		List<String> alunosDownload = Util.alunosDownload(idAtividade);
		fezDownload = alunosDownload.contains(matricula);
		
		return fezDownload;
	}

	private Student buscarAluno(String matricula) throws ConfigurationException, IOException, ServiceException, StudentException {
		Student aluno = null;
		Map<String,Student> alunos = Configuration.getInstance().getStudents();
		aluno = alunos.get(matricula);
		if(aluno == null){
			throw new StudentException("Submission.buscarAluno() - aluno " + matricula + " nao cadastrado");
		}
		return aluno;
	}

	private Atividade buscarAtividade(String idAtividade) throws ConfigurationException, IOException, ServiceException, AtividadeException {
		Atividade atividade = Configuration.getInstance().getAtividades().get(idAtividade);
		if (atividade == null) {
			throw new AtividadeException("Submission.buscarAtividade() - Atividade " + idAtividade + " nao cadastrada");
		}
		return atividade;
	}

	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public Student getAluno() {
		return aluno;
	}

	public void setAluno(Student aluno) {
		this.aluno = aluno;
	}

	public GregorianCalendar getData() {
		return data;
	}

	public void setData(GregorianCalendar data) {
		this.data = data;
	}

	public boolean isFezDownload() {
		return fezDownload;
	}

	public void setFezDownload(boolean fezDownload) {
		this.fezDownload = fezDownload;
	}

	public File getArquivoSubmetido() {
		return arquivoSubmetido;
	}

	public void setArquivoSubmetido(File arquivoSubmetido) {
		this.arquivoSubmetido = arquivoSubmetido;
	}
	
	
}
