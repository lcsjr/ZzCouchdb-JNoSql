package br.com.thomsonreuters.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.thomsonreuters.model.Usuario;
import br.com.thomsonreuters.model.repository.UsuarioRepositorio;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UsuarioRepositorio userRepositorio;

	@Autowired
	private BCryptPasswordEncoder bCrypt;

//	public UserController(UsuarioRepositorio applicationUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
//		this.userRepositorio = applicationUserRepository;
//		this.bCrypt = bCryptPasswordEncoder;
//	}
	
	/*
	 * A implementação deste endpoint é somente para criptografar a senha do usuario
	 */
	@PostMapping("/sign-up")
	public void signUp( @RequestBody Usuario user) {
		user.setPassword( bCrypt.encode(user.getPassword() ));
		userRepositorio.save(user);
	}

}
