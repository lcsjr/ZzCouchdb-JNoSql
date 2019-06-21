package br.com.thomsonreuters.auth0.security;

import static br.com.thomsonreuters.auth0.security.SecurityConstants.HEADER_STRING;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.SECRET;
import static br.com.thomsonreuters.auth0.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String header = request.getHeader(HEADER_STRING);
		
		if (header == null || !header.startsWith(TOKEN_PREFIX) ) {
		
			chain.doFilter(request, response);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthenticatio(request);
		
		
		
		super.doFilterInternal(request, response, chain);
	}

	private UsernamePasswordAuthenticationToken getAuthenticatio(HttpServletRequest request) {

		String token =request.getHeader(HEADER_STRING);
		System.out.println("token: " + token);
		
		if (token != null) {
			//parse the token
			String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
					.build()
					.verify(token.replace(TOKEN_PREFIX, ""))
					.getSubject();
			System.out.println("user: "+user);
			
			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}
			return null;
		}
		
		return null;
	}

}
