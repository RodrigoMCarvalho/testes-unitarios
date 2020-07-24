package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Test
	public void teste() {
		
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		Assert.assertEquals("Erro de comparação: ",1, 2);  //String só irá aparecer se ocorrer falha
		Assert.assertEquals(2.1234, 2.1234569, 0.0001);  //delta eh a margem de erro
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		
		int n1 = 5;
		Integer n2 = 5;
		Assert.assertEquals(Integer.valueOf(n1), n2);
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("b"));
		Assert.assertEquals("bola","bola");
		Assert.assertNotEquals("bola","casa");
		
		Usuario u1 = new Usuario("Usuario 1");
		Usuario u2 = new Usuario("Usuario 1");
		Usuario u3 = null;
		
		Assert.assertEquals(u1, u2);
		Assert.assertSame(u1, u1);  //compara instancias do mesmo objeto
		Assert.assertNotSame(u1, u2);
		Assert.assertNull(u3);
		
	
		
	}
}
