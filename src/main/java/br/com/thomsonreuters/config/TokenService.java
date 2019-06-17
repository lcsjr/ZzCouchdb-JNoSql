package br.com.thomsonreuters.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import br.com.thomsonreuters.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenService {
	
	@Value(value = "${forum.jwt.expiration}")
	private String expiration;
	
	@Value(value = "${forum.jwt.secret}")
	private String secret;

	public String gerarToken(Authentication authentication) {
		
		Usuario logado = (Usuario) authentication.getPrincipal();
		Date now = new Date();
		Date expira = new Date(now.getTime() + Long.parseLong(expiration) );
		
		return Jwts.builder()
			.setIssuer("API de Interface Thomson Reuters.")
			.setSubject(logado.getId().toString())
			.setIssuedAt(now)
			.setExpiration(expira)
			.signWith(SignatureAlgorithm.HS256, secret)	
			.compact();
		
	}

}
