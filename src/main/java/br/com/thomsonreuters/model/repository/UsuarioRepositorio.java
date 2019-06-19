package br.com.thomsonreuters.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.thomsonreuters.model.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
	
	Usuario findByUsername(String username);

}
