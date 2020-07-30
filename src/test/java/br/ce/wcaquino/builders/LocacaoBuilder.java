package br.ce.wcaquino.builders;

import java.util.Arrays;
import java.util.Date;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoBuilder {
	
	private Locacao locacao;
	private LocacaoBuilder(){}

	public static LocacaoBuilder umLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		inicializarDadosPadroes(builder);
		return builder;
	}

	public static void inicializarDadosPadroes(LocacaoBuilder builder) {
		builder.locacao = new Locacao();
		Locacao locacao = builder.locacao;
		
		locacao.setUsuario(UsuarioBuilder.umUsuario().agora());
		locacao.setFilmes(FilmeBuilder.listFilme().build());
		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
		locacao.setValor(4.0);
	}

	public LocacaoBuilder comUsuario(Usuario param) {
		locacao.setUsuario(param);
		return this;
	}

	public LocacaoBuilder comListaFilmes(Filme... params) {
		locacao.setFilmes(Arrays.asList(params));
		return this;
	}

	public LocacaoBuilder comDataLocacao(Date param) {
		locacao.setDataLocacao(param);
		return this;
	}

	public LocacaoBuilder comDataRetorno(Date param) {
		locacao.setDataRetorno(param);
		return this;
	}

	public LocacaoBuilder comValor(Double param) {
		locacao.setValor(param);
		return this;
	}

	public Locacao agora() {
		return locacao;
	}
}
