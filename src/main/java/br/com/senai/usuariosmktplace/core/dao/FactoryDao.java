package br.com.senai.usuariosmktplace.core.dao;

import br.com.senai.usuariosmktplace.core.dao.postgres.DaoPostegresUsuario;

public class FactoryDao {
	
	private static FactoryDao instance;
	
	private FactoryDao() {}
	
	
	public DaoUsuario getDaoUsuario() {
		return new DaoPostegresUsuario();
	}
	
	public static FactoryDao getInstance() {
		if(instance == null) {
			instance = new FactoryDao();
		}
		return instance;
	}

}
