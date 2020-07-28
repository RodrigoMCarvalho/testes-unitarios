package br.ce.wcaquino.excecoes;

public class FilmeSemEstoqueException extends Exception {

	private static final long serialVersionUID = 1L;

	public FilmeSemEstoqueException(String mensagem) {
		super(mensagem);
	}
}
