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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

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

		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

		String token =request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "").trim();
		
		if (token != null) {
			//parse the token
			DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET))
					.build()
					.verify(token);
			
			String user = decodedJWT.getSubject();
			System.out.println("user: "+user);			
			
			if ( decodedJWT.getClaims().containsKey("company") )
				System.out.println( "company : " + decodedJWT.getClaims().get("company").asString()  );
			
			if ( decodedJWT.getClaims().containsKey("userName"))
				System.out.println( "Nome : " + decodedJWT.getClaims().get("userName").asString()  );
			
			if ( !decodedJWT.getHeaderClaim("owner").asString().isEmpty() )
				System.out.println( "owner: " + decodedJWT.getHeaderClaim("owner").asString() );
			
			System.out.println( "Header:   " +decodedJWT.getHeader() );
			System.out.println( "Subject:  " +user );
			System.out.println( "Audience: " +decodedJWT.getAudience() );
			System.out.println( "IssuedAt: " +decodedJWT.getIssuedAt() );
			System.out.println( "Expires:  " +decodedJWT.getExpiresAt() );
			System.out.println( "Issuer:   " +decodedJWT.getIssuer() );
			
			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}
			return null;
		}
		return null;
	}

}
