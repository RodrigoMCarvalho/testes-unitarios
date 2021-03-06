package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.excecoes.FilmeSemEstoqueException;
import br.ce.wcaquino.excecoes.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	private static Double DESCONTO_25_PORCENTO = 0.75d;
	private static Double DESCONTO_50_PORCENTO = 0.50d;
	private static Double DESCONTO_75_PORCENTO = 0.25d;
	private static Double DESCONTO_100_PORCENTO = 0d;

	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException {
		
		if(filmes == null) {
			throw new LocadoraException("Filme vazio");
		}
		
		for (Filme filme: filmes) {
			if(filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException("Filme sem estoque");
			}
		}
		
		if(usuario == null) {
			throw new LocadoraException("Usu�rio vazio");
		}
		
		boolean negaticado;
		try {
			negaticado = spcService.possuiNegaticacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas no SPC, tente novamente");
		}
		
		if(negaticado) {
			throw new LocadoraException("Usuario negativado");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(new ArrayList<Filme>(filmes));
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(obterData());
		locacao.setValor(calcularValorLocacao(filmes));
		
		//Entrega no dia seguinte
		Date dataEntrega = obterData();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		dao.salvar(locacao);
		
		return locacao;
	}

	protected Date obterData() {
		return new Date();
	}

	private Double calcularValorLocacao(List<Filme> filmes) {
		Double valorTotal = 0d;
		for (int i = 0; i < filmes.size(); i++) {   //recebe desconto a partir do 3 filme alugado
			Filme filme = filmes.get(i);
			Double valorFilmeDesconto = filme.getPrecoLocacao();
			switch (i) {
			case 2:valorFilmeDesconto = valorFilmeDesconto * DESCONTO_25_PORCENTO; break;
			case 3:valorFilmeDesconto = valorFilmeDesconto * DESCONTO_50_PORCENTO; break;
			case 4:valorFilmeDesconto = valorFilmeDesconto * DESCONTO_75_PORCENTO; break;
			case 5:valorFilmeDesconto = DESCONTO_100_PORCENTO; break;
			default:
				break;
			}
			valorTotal+=valorFilmeDesconto;
		}
		return valorTotal;
	}
	
	public void notificarAtraso() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for(Locacao locacao: locacoes) {
			if(locacao.getDataRetorno().before(obterData())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}

	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(obterData());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		dao.salvar(novaLocacao);
	}
	

	
	
	
	
	
	
	
	
	
}