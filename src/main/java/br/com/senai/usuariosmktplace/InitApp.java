package br.com.senai.usuariosmktplace;

import br.com.senai.usuariosmktplace.core.service.UsuarioService;

public class InitApp {

	public static void main(String[] args) {
		UsuarioService service = new UsuarioService();
		System.out.println(service.removerAcentoDo("José da silva"));
		System.out.println(service.fracionar("José da Silva Alburquerque dos Anjos e Bragança"));
		System.out.println(service.gerarLoginPor("José da Silva dos Anjos"));
		System.out.println(service.gerarHashDa("jose123456"));
	}
}
