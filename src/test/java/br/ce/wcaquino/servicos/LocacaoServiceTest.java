package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
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
	
	@InjectMocks
	private LocacaoService locacaoService;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spc;
	
	@Mock
	private EmailService email;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);  // ou @RunWith(MockitoJUnitRunner.class)
		/*locacaoService = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		locacaoService.setDao(dao);
		spc = Mockito.mock(SPCService.class);
		locacaoService.setSpcService(spc);
		email = Mockito.mock(EmailService.class);
		locacaoService.setEmailService(email);*/
		
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
		//Assert.fail("Não deveria lançar exceção");

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
			Assert.fail("Deveria lançar exceção");
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
			Assert.assertThat(e.getMessage(), is("Usuário vazio"));
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
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = FilmeBuilder.listFilme().build();
		
		when(spc.possuiNegaticacao(any(Usuario.class))).thenReturn(true);  //quando "possuiNegaticacao" for chamado, para qualquer usuario, 
																			//então retorne true
		//exception.expect(LocadoraException.class);
		//exception.expectMessage("Usuario negativado");
		
		//acao
		try {
			locacaoService.alugarFilme(usuario, filmes);
		//verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario negativado"));
			e.printStackTrace();
		} 
		
		verify(spc).possuiNegaticacao(usuario);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = FilmeBuilder.listFilme().build();
		
		when(spc.possuiNegaticacao(usuario)).thenThrow(new Exception("Falha no serviço de SPC")); //simulacao de retorno
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas no SPC, tente novamente");
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
		
	}

	@Test
	public void deveEnviarEmailParaLocacaoesAtrasadas() {
		//cenario
		Usuario usuario1 = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Usuario 3").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().comUsuario(usuario1).agora(),
				umLocacao().atrasado().comUsuario(usuario2).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora());
		
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		locacaoService.notificarAtraso();
		
		//verificacao
		verify(email, times(3)).notificarAtraso(any(Usuario.class));
		verify(email, never()).notificarAtraso(usuario1); //usuario1 não tem atraso, então ele nunca vai receber email de atraso
		verify(email).notificarAtraso(usuario2);
		verify(email, atLeastOnce()).notificarAtraso(usuario3); //vai ser invocado pelo menos 1x
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Locacao locacao = umLocacao().comUsuario(usuario).agora();
		
		//acao
		locacaoService.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class); 
		verify(dao).salvar(argCapt.capture()); //captura o novaLocacao
		Locacao locacaoRetornada = argCapt.getValue();
		
		//mostra uma colecao de erros, caso usasse o Assert.assertThat iria mostrar apenas o 1º erro
		error.checkThat(locacaoRetornada.getValor(), is(12.0));   //multiplica pelo dias adicionados
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}

	
	/*public static void main(String[] args) {
		new buildermaster.BuilderMaster().gerarCodigoClasse(Locacao.class);
	}*/
	
	
	
	
	
	
	
}
