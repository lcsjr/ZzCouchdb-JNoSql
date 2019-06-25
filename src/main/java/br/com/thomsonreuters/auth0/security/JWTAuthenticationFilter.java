package br.com.thomsonreuters.auth0.security;

import static br.com.thomsonreuters.auth0.security.SecurityConstants.EXPIRATION_TIME;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.HEADER_STRING;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.SECRET;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.thomsonreuters.model.ApplicationUser;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			ApplicationUser creds = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
			UsernamePasswordAuthenticationToken usrTk = new UsernamePasswordAuthenticationToken( 
													creds.getUsername(), creds.getPassword(), new ArrayList<>() ); 
			
			return authenticationManager.authenticate(usrTk);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
											FilterChain chain, Authentication authResult) throws IOException, ServletException {

		Map<String, Object> header = new HashMap<String, Object>();
		header.put("owner", "SCHEMA1");
		
		String token = JWT.create()
				.withHeader(header)
				.withSubject( ((User)authResult.getPrincipal()).getUsername() )
				.withIssuer("Thomson Reuters Brazil")
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt( new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.withAudience("Nome do Cliente - AVALIAR")
				.withClaim("userName", "Luiz Carlos Santos Jr")
				.sign( Algorithm.HMAC256(SECRET) );
		

		System.out.println("------------------------------------------------------------------------");
		System.out.println(HEADER_STRING + " : " + TOKEN_PREFIX +" "+ token);
		System.out.println("------------------------------------------------------------------------");
					
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + token);
				
	}
	
}
