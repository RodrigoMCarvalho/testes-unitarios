package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	private LocacaoService locacaoService;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void before() {
		locacaoService = new LocacaoService();
		//System.out.println("Before");
	}
	
	/*@After
	public void after() {
		locacaoService = new LocacaoService();
		System.out.println("After");
	}
	
	@BeforeClass
	public static void beforeClass() {
		System.out.println("BeforeClass");
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println("AfterClass");
	}*/

	@Test
	public void testeLocacao() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0), 
				   						   new Filme("Filme 2", 2, 5.0));
		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		// verificao
		Assert.assertEquals(locacao.getValor(), 10.0, 0.2);
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));

		assertThat(locacao.getValor(), is(10.0));
		assertThat(locacao.getValor(), is(not(6.0)));
		Assert.assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));

		error.checkThat(locacao.getValor(), is(10.0));

		//Assert.fail("Não deveria lançar exceção");

	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testLocacaoFilmeSemEstoque() throws Exception{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0), 
										   new Filme("Filme 2", 2, 5.0));
		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	//@Test
	public void testLocacaoFilmeSemEstoque2() throws LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0), 
										   new Filme("Filme 2", 2, 5.0));
		// acao
		try {
			locacaoService.alugarFilme(usuario, filmes);
			Assert.fail("Deveria lançar exceção");
		} catch (FilmeSemEstoqueException e) {
			e.printStackTrace();
			Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
		}
	}
	
	@Test
	public void testLocacaoFilmeSemEstoque3() throws FilmeSemEstoqueException, LocadoraException{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0), 
				   						   new Filme("Filme 2", 2, 5.0));
		
		exception.expect(FilmeSemEstoqueException.class);
		//exception.expectMessage("Filme sem estoque");

		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void testLocacaoUsuarioVazio() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0), 
										   new Filme("Filme 2", 2, 5.0));
		//acao
		try {
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário vazio"));
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLocacaoFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		//acao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		locacaoService.alugarFilme(usuario, null);
	}
	
	
	
	
	
}
