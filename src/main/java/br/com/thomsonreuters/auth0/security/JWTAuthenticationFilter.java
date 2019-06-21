package br.com.thomsonreuters.auth0.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.thomsonreuters.model.ApplicationUser; 

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import static br.com.thomsonreuters.auth0.security.SecurityConstants.EXPIRATION_TIME;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.HEADER_STRING;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.SECRET;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			ApplicationUser creds = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
			
			System.out.println(creds);
			
			UsernamePasswordAuthenticationToken usrTk = new UsernamePasswordAuthenticationToken(
					creds.getUsername(), creds.getPassword(), new ArrayList<>() ); 
			
			System.out.println(usrTk);
			
			Authentication atoken = authenticationManager.authenticate(usrTk);
			
			System.out.println(atoken);
			
			return atoken;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
											FilterChain chain, Authentication authResult) throws IOException, ServletException {

		String token = JWT.create()
				.withSubject( ((User)authResult.getPrincipal()).getUsername() )
				.withExpiresAt( new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.sign(  HMAC512(SECRET.getBytes()) );
		
		System.out.println(token);
		System.out.println(HEADER_STRING + " : " + TOKEN_PREFIX + token);
					
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
				
	}
	
}
