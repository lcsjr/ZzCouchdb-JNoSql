package br.com.thomsonreuters.model;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.thomsonreuters.model.repository.ApplicationUserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	private ApplicationUserRepository  applicationUserRepository ;

	UserDetailsServiceImpl(ApplicationUserRepository  applicationUserRepository){
		this.applicationUserRepository = applicationUserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		ApplicationUser user = applicationUserRepository.findByUsername(username);
		
		if(user == null) {
			throw new UsernameNotFoundException(username);
		}
		
		return new User(user.getUsername(), user.getPassword(), new ArrayList<>() )	;
	}


}
