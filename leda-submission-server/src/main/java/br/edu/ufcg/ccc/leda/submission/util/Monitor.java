package br.edu.ufcg.ccc.leda.submission.util;

public class Monitor implements Corretor {
	private String matricula;
	private String nome;
	private String email;
	private String fone;
	private String senha;
	
	public Monitor(String matricula, String nome, String email, String fone, String senha) {
		super();
		this.matricula = matricula;
		this.nome = nome;
		this.email = email;
		this.fone = fone;
		this.senha = senha;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#getMatricula()
	 */
	@Override
	public String getMatricula() {
		return matricula;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#setMatricula(java.lang.String)
	 */
	@Override
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#getNome()
	 */
	@Override
	public String getNome() {
		return nome;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#setNome(java.lang.String)
	 */
	@Override
	public void setNome(String nome) {
		this.nome = nome;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#getFone()
	 */
	@Override
	public String getFone() {
		return fone;
	}
	/* (non-Javadoc)
	 * @see br.edu.ufcg.ccc.leda.submission.util.Corretor#setFone(java.lang.String)
	 */
	@Override
	public void setFone(String fone) {
		this.fone = fone;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.getNome().split(" ")[0] + ")";
	}
	
	
	
	
}
