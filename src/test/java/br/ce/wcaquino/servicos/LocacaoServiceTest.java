package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.excecoes.FilmeSemEstoqueException;
import br.ce.wcaquino.excecoes.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	private LocacaoService locacaoService;
	private LocacaoDAO dao;
	private SPCService spc;
	private EmailService email;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void before() {
		locacaoService = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		locacaoService.setDao(dao);
		spc = Mockito.mock(SPCService.class);
		locacaoService.setSpcService(spc);
		email = Mockito.mock(EmailService.class);
		locacaoService.setEmailService(email);
		
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
	public void deveAlugarFilmeComSucesso() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().comValor(5.0).agora(), 
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
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		//Assert.fail("N�o deveria lan�ar exce��o");

	}

	@Test(expected=FilmeSemEstoqueException.class)
	public void deveLancarFilmeSemEstoqueException() throws Exception{
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0), 
										   new Filme("Filme 2", 2, 5.0));
		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	//@Test
	public void naoDeveAlugarFilmeSemEstoque() throws LocadoraException {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0), 
										   new Filme("Filme 2", 2, 5.0));
		// acao
		try {
			locacaoService.alugarFilme(usuario, filmes);
			Assert.fail("Deveria lan�ar exce��o");
		} catch (FilmeSemEstoqueException e) {
			e.printStackTrace();
			Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemEstoque2() throws FilmeSemEstoqueException, LocadoraException{
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().semEstoque().agora(), 
										   FilmeBuilder.umFilme().agora());
		
		exception.expect(FilmeSemEstoqueException.class);
		//exception.expectMessage("Filme sem estoque");

		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void naoDeveAlugarFilmeParaUsuarioVazio() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = FilmeBuilder.listFilme().build();
		
		//acao
		try {
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usu�rio vazio"));
			e.printStackTrace();
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		
		//acao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		locacaoService.alugarFilme(usuario, null);
	}
	
	@Test
	public void devePagar75PorCentoNoFilme3() throws LocadoraException, FilmeSemEstoqueException {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora(), 
										   FilmeBuilder.umFilme().agora(),
										   FilmeBuilder.umFilme().agora());
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificao
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(11.0));
	}
	
	@Test
	public void devePagar50PorCentoNoFilme4() throws LocadoraException, FilmeSemEstoqueException {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0), 
										   new Filme("Filme 2", 2, 4.0),
										   new Filme("Filme 3", 2, 4.0),
										   new Filme("Filme 4", 2, 4.0));
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificao
		//4+4+3+2
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(13.0));
	}
	
	@Test
	public void devePagar25PorCentoNoFilme5() throws LocadoraException, FilmeSemEstoqueException {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = FilmeBuilder.listFilme().build();
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificao
		//4+4+3+2+1
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(14.0));
	}
	
	@Test
	public void devePagarZeroNoFilme6() throws LocadoraException, FilmeSemEstoqueException {
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = FilmeBuilder.listFilme().build();
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificao
		//4+4+3+2+1+0
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(14.0));
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		//assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.SUNDAY));
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.SUNDAY));
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws LocadoraException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = FilmeBuilder.listFilme().build();
		
		when(spc.possuiNegaticacao(usuario)).thenReturn(true);  //quando "possuiNegaticacao" for chamado, ent�o retorne true
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuario negativado");
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	public void deveEnviarEmailParaLocacaoesAtrasadas() {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao()
					.comUsuario(usuario)
					.comDataLocacao(DataUtils.obterDataComDiferencaDias(-2))
					.agora());
		
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		locacaoService.notificarAtraso();
		
		//verificacao
		Mockito.verify(email).notificarAtraso(usuario);
	}

	
	/*public static void main(String[] args) {
		new buildermaster.BuilderMaster().gerarCodigoClasse(Locacao.class);
	}*/
	
	
	
	
	
	
	
}
