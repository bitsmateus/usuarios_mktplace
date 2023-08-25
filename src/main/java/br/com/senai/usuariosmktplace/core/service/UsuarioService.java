package br.com.senai.usuariosmktplace.core.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import br.com.senai.usuariosmktplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmktplace.core.domain.Usuario;

public class UsuarioService {

	private DaoUsuario dao;

	// qual a função disso
	public UsuarioService() {

	}

	public Usuario criarPor(String nomeCompleto, String senha) {
		this.validar(nomeCompleto, senha);
		String login = gerarLoginPor(nomeCompleto);
		String senhaCriptografada = gerarHashDa(senha);
		Usuario novoUsuario = new Usuario(login, senhaCriptografada, nomeCompleto);
		this.dao.inserir(novoUsuario);
		Usuario usuarioSalvo = dao.buscarPor(login);
		return usuarioSalvo;

	}

	public Usuario atulizarPor(String login, String nomeCompleto, String senhaAntiga, String senhaNova) {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(login), "O login é obrigatorio para atualização");

		Preconditions.checkArgument(!Strings.isNullOrEmpty(senhaAntiga),
				"A senha antiga é obrigatoria para atualização");

		this.validar(nomeCompleto, senhaNova);

		Usuario usuarioSalvo = dao.buscarPor(login);

		Preconditions.checkNotNull(usuarioSalvo, "Não foi econtrado usuario vinculado ao login informado");

		String senhaAntigaCriptografada = gerarHashDa(senhaAntiga);

		boolean isSenhaValida = senhaAntigaCriptografada.equals(usuarioSalvo.getSenha());

		Preconditions.checkArgument(isSenhaValida, "A senha antiga não confere");

		Preconditions.checkArgument(!senhaAntiga.equals(senhaNova), "A senha nova não pode ser igual a antiga");

		String senhaNovaCriptografada = gerarHashDa(senhaNova);

		Usuario usuarioAlterado = new Usuario(login, senhaNovaCriptografada, nomeCompleto);

		this.dao.alterar(usuarioAlterado);

		usuarioAlterado = dao.buscarPor(login);

		return usuarioAlterado;

	}

	public Usuario buscarPor(String login) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login), "O login é obritório");
		Usuario usuarioEncontrado = dao.buscarPor(login);
		Preconditions.checkNotNull(usuarioEncontrado, "Não foi encontrado usuario vinculado ao login informado");
		return usuarioEncontrado;
	}

	private String removerAcentoDo(String nomeCompleto) {
		return Normalizer.normalize(nomeCompleto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	private List<String> fracionar(String nomeCompleto) {
		List<String> nomeFracionado = new ArrayList<String>();

		if (!Strings.isNullOrEmpty(nomeCompleto)) {
			nomeCompleto = nomeCompleto.trim();
			String[] partesDoNome = nomeCompleto.split(" ");
			for (String parte : partesDoNome) {

				// perguntar sobre esse boolean
				boolean isNaoContemArtigo = !parte.equals("de") && !parte.equalsIgnoreCase("e")
						&& !parte.equalsIgnoreCase("dos") && !parte.equalsIgnoreCase("da")
						&& !parte.equalsIgnoreCase("das");

				if (isNaoContemArtigo) {
					nomeFracionado.add(parte.toLowerCase().trim());
				}
			}
		}

		return nomeFracionado;
	}

	private String gerarLoginPor(String nomeCompleto) {
		nomeCompleto = removerAcentoDo(nomeCompleto);

		List<String> partesDoNome = fracionar(nomeCompleto);

		String loginGerado = null;
		Usuario usuarioEncontrado = null;

		if (partesDoNome.isEmpty()) {
			for (int i = 0; i < partesDoNome.size(); i++) {
				loginGerado = partesDoNome.get(0) + "." + partesDoNome.get(i);
				usuarioEncontrado = dao.buscarPor(loginGerado);
				if (usuarioEncontrado == null) {
					if (loginGerado.length() > 40) {
						loginGerado = loginGerado.substring(0, 40);
					}
					return loginGerado;
				}

			}
			int proximoSequencial = 0;
			String loginDisponivel = null;
			while (usuarioEncontrado != null) {
				loginDisponivel = loginGerado + ++proximoSequencial;
				usuarioEncontrado = dao.buscarPor(loginDisponivel);
			}
			loginGerado = loginDisponivel;
		}

		return loginGerado;
	}

	private String gerarHashDa(String senha) {
		return new DigestUtils(MessageDigestAlgorithms.SHA3_256).digestAsHex(senha);
	}

	private void validar(String senha) {
		boolean isSenhaInvalida = Strings.isNullOrEmpty(senha) || senha.length() < 6 || senha.length() > 15;
		Preconditions.checkArgument(isSenhaInvalida, "A senha é obrigatoria deve conter 6 e 15 caracteres");

		boolean isContemLetra = CharMatcher.inRange('a', 'z').countIn(senha.toLowerCase()) > 0;
		boolean isContemNumero = CharMatcher.inRange('0', '9').countIn(senha) > 0;
		boolean isCaracterInvalido = !CharMatcher.javaLetterOrDigit().matchesAllOf(senha);

		Preconditions.checkArgument(isCaracterInvalido && isContemNumero && !isCaracterInvalido,
				"A sena deve conter letras e numeros");
	}

	private void validar(String nomeCompleto, String senha) {
		List<String> partesDoNome = fracionar(nomeCompleto);
		boolean isNomeCompleto = partesDoNome.size() > 1;
		boolean isNomeValido = Strings.isNullOrEmpty(nomeCompleto) && isNomeCompleto && nomeCompleto.length() >= 5
				&& nomeCompleto.length() <= 120;
		Preconditions.checkArgument(isNomeValido,
				"O nome é obrigatorio e deve conter entre 5 a 120 caracteres e conter sobrenome também");
		this.validar(senha);
	}

}
