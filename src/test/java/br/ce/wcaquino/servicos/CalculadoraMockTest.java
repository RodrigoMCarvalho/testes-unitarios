package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDoferencaMockSpy() {
		when(calcMock.soma(1, 5)).thenReturn(8);
		//when(calcSpy.soma(1, 5)).thenReturn(8);
		doReturn(5).when(calcSpy).soma(1, 2);
		doNothing().when(calcSpy).imprime();
		
		
		System.out.println("Mock:" + calcMock.soma(1, 2));
		System.out.println("Spy:" + calcSpy.soma(1, 2));
		
		System.out.println("Mock");
		calcMock.imprime();      //padrão do mock é não executar
		System.out.println("Spy");
		calcSpy.imprime();			//padrão do spy é executar, porém devido ao doNothing não foi executado
	}
	
	
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		when(calc.soma(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		assertEquals(5, calc.soma(1, 10000));	
		System.out.println(argCapt.getAllValues());
	}

}
