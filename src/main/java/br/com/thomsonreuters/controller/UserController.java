package br.com.thomsonreuters.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.thomsonreuters.model.ApplicationUser;
import br.com.thomsonreuters.model.repository.ApplicationUserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private ApplicationUserRepository userRepositorio;

	@Autowired
	private BCryptPasswordEncoder bCrypt;

	/*
	 * A implementação deste endpoint é somente para criptografar a senha do usuario
	 */
	@PostMapping("/sign-up")
	@ResponseBody
	public String signUp( @RequestBody ApplicationUser user) {
		user.setPassword( bCrypt.encode(user.getPassword() ));
		userRepositorio.save(user);
		
		return user.toString();
	}

}
