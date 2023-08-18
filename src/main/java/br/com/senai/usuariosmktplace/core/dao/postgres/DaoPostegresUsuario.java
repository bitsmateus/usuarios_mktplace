package br.com.senai.usuariosmktplace.core.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.senai.usuariosmktplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmktplace.core.dao.ManagerDb;
import br.com.senai.usuariosmktplace.core.domain.Usuario;

public class DaoPostegresUsuario implements DaoUsuario {

	private final String INSERT = "INSERT INTO usuarios (login, nome, senha) VALUES (?, ?, ?)";

	private final String UPDATE = "UPDATE usuarios SET nome = ?, senha = ? WHERE login = ?";

	private final String SELECT_BY_LOGIN = "SELECT u.login, u.nome, u.senha " + "FROM usuarios u "
			+ "WHERE u.login = ?";

	private Connection conexao;

	public DaoPostegresUsuario() {
		this.conexao = ManagerDb.getInstance().getConexao();

	}

	@Override
	public void inserir(Usuario usuario) {
		PreparedStatement ps = null;
		try {

			ps = conexao.prepareStatement(INSERT);
			ps.setString(1, usuario.getLogin());
			ps.setString(2, usuario.getNomeCompleto());
			ps.setString(3, usuario.getSenha());
			ps.execute();

			// manda para o banco

		} catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro ao inserir o usuario. Motivo: " + e.getMessage());
		} finally {
			ManagerDb.getInstance().fechar(ps);
		}
	}

	@Override
	public void alterar(Usuario usuario) {

		PreparedStatement ps = null;
		try {

			ManagerDb.getInstance().configurarAutocommitDa(conexao, false);
			ps = conexao.prepareStatement(UPDATE);
			ps.setString(1, usuario.getLogin());
			ps.setString(2, usuario.getNomeCompleto());
			ps.setString(3, usuario.getSenha());
			boolean isAlteracaoOK = ps.executeUpdate() == 1;
			if (isAlteracaoOK) {
				this.conexao.commit();
			} else {
				this.conexao.rollback();
			}
			ManagerDb.getInstance().configurarAutocommitDa(conexao, true);

			// manda para o banco

		} catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro ao inserir o usuario. Motivo: " + e.getMessage());
		} finally {
			ManagerDb.getInstance().fechar(ps);
		}
	}

	@Override
	public Usuario buscarPor(String login) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conexao.prepareStatement(SELECT_BY_LOGIN);
			ps.setString(1, login);
			rs = ps.executeQuery();
			if (rs.next()) {
				return extrairDo(rs);
			}
			boolean isAlteracaoOK = ps.executeUpdate() == 1;
			if (isAlteracaoOK) {
				this.conexao.commit();
			} else {
				this.conexao.rollback();
			}
			ManagerDb.getInstance().configurarAutocommitDa(conexao, true);
			// manda para o banco
		} catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro ao inserir o usuario. Motivo: " + e.getMessage());
		} finally {
			ManagerDb.getInstance().fechar(ps);
			ManagerDb.getInstance().fechar(rs);
		}
		return null;
	}

	private Usuario extrairDo(ResultSet rs) {
		try {
			String login = rs.getString("login");
			String senha = rs.getString("senha");
			String nomeCompleto = rs.getString("nome");
		} catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro ao extrar o usuario. Motivo: " + e.getMessage());
		}
		return null;
	}
}
