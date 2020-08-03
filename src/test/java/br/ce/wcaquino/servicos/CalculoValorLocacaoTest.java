package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.excecoes.FilmeSemEstoqueException;
import br.ce.wcaquino.excecoes.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest { 
	
	@Parameter  //valor 0
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;
	
	@InjectMocks
	private LocacaoService locacaoService;
	
	@Mock
	private SPCService spc;
	
	@Mock
	private LocacaoDAO dao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	private static Filme filme1= FilmeBuilder.umFilme().agora();
	private static Filme filme2= FilmeBuilder.umFilme().agora();
	private static Filme filme3= FilmeBuilder.umFilme().agora();
	private static Filme filme4= FilmeBuilder.umFilme().agora();
	private static Filme filme5= FilmeBuilder.umFilme().agora();
	private static Filme filme6= FilmeBuilder.umFilme().agora();

	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[] [] {
				{Arrays.asList(filme1, filme2), 8.0, "2 filmes: Sem desconto"},
				{Arrays.asList(filme1, filme2, filme3), 11.0, "3 filmes: 25%"},
				{Arrays.asList(filme1, filme2, filme3,filme4), 13.0, "4 filmes: 50%"},
				{Arrays.asList(filme1, filme2, filme3,filme4, filme5), 14.0, "5 filmes: 75%"},
				{Arrays.asList(filme1, filme2, filme3,filme4, filme5, filme6), 14.0, "6 filmes: 100%"}
		});
	}

	@Test
	public void deveCalcularValorLocacaConsiderandoDescontos() throws LocadoraException, FilmeSemEstoqueException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, this.filmes);
		
		//verificao
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(this.valorLocacao));
	}
	
	@Test
	public void print() {
		System.out.println(valorLocacao);
	}
}
