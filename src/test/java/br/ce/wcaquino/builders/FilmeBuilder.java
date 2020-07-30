package br.ce.wcaquino.builders;

import java.util.Arrays;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {

	private Filme filme;
	private List<Filme> filmes;
	
	private FilmeBuilder() {}
	
	public static FilmeBuilder umFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setNome("Filme 1");
		builder.filme.setEstoque(2);
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public static FilmeBuilder listFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0), 
				   new Filme("Filme 2", 2, 4.0),
				   new Filme("Filme 3", 2, 4.0),
				   new Filme("Filme 4", 2, 4.0),
				   new Filme("Filme 5", 2, 4.0),
				   new Filme("Filme 6", 2, 4.0));
		return builder;
	}
	
	public FilmeBuilder comValor(Double precoLocacao) {
		filme.setPrecoLocacao(precoLocacao);
		return this;
	}
	
	public FilmeBuilder semEstoque() {
		filme.setEstoque(0);
		return this;
	}
	
	public Filme agora() {
		return this.filme;
	}
	
	public List<Filme> build() {
		return this.filmes;
	}
	
	
	
	
	
	
	
	
	
	
}
