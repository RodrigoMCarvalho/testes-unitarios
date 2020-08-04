package br.ce.wcaquino.servicos;

public class Calculadora {

	public int soma(int a, int b) {
		System.out.println("Método soma");
		return a + b;
	}

	public int substrair(int a, int b) {
		return a - b;
	}
	
	public void imprime() {
		System.out.println("Passei aqui");
	}

}
