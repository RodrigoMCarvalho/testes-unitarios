package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalculadoraTest {
	
	Calculadora calc;
	
	@Before
	public void setup() {
		calc = new Calculadora();
	}
	
	@Test
	public void deveSomarDoisNumeros() {
		//cenario
		int a = 5;
		int b = 3;
		
		//acao
		int resultado = calc.soma(a, b);
		
		//verificacao
		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void deveSubstrairDoisNumeros() {
		//cenario
		int a = 5;
		int b = 3;
		
		//acao
		int resultado = calc.substrair(a, b);
		
		//verificacao
		Assert.assertEquals(2, resultado);
	}
}
