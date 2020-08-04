package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class CalculadoraMockTest {
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		when(calc.soma(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		assertEquals(5, calc.soma(1, 10000));	
		System.out.println(argCapt.getAllValues());
		
	}

}