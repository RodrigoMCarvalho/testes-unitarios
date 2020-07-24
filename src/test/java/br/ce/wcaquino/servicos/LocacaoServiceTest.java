package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testeLocacao() throws Exception {
		// cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);

		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);
		
		// verificao
		Assert.assertEquals(locacao.getValor(), 5.0, 0.2);
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));

		assertThat(locacao.getValor(), is(5.0));
		assertThat(locacao.getValor(), is(not(6.0)));
		Assert.assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));

		error.checkThat(locacao.getValor(), is(5.0));

		//Assert.fail("Não deveria lançar exceção");

	}
	
	@Test(expected=Exception.class)
	public void testLocacaoFilmeSemEstoque() throws Exception{
		// cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		// acao
		locacaoService.alugarFilme(usuario, filme);
	}
	
	@Test
	public void testLocacaoFilmeSemEstoque2() {
		// cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		try {
			locacaoService.alugarFilme(usuario, filme);
			Assert.fail("Deveria lançar exceção");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
		}
	}
	
	@Test
	public void testLocacaoFilmeSemEstoque3() throws Exception{
		// cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");

		// acao
		locacaoService.alugarFilme(usuario, filme);
	}
	
	
}
