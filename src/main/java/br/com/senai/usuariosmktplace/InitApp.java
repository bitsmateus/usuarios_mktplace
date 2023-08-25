package br.com.senai.usuariosmktplace;

import br.com.senai.usuariosmktplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmktplace.core.dao.FactoryDao;
import br.com.senai.usuariosmktplace.core.domain.Usuario;

public class InitApp {

	public static void main(String[] args) {
		DaoUsuario dao = FactoryDao.getInstance().getDaoUsuario();

		Usuario usuario = new Usuario("jose.silva", "José da silva", "jose2023");
		System.out.println(usuario.getLogin());

	}
}
