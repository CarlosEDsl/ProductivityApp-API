package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.repositories.UserRepository;
import com.eduardocarlos.productivityApp.security.UserDetailsImpl;
import com.eduardocarlos.productivityApp.services.exceptions.ObjectNotFoundException;
import com.eduardocarlos.productivityApp.services.exceptions.UnauthenticatedUserException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsImplService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsImplService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new ObjectNotFoundException("Email not found for login");
        }
        return new UserDetailsImpl(user.get());
    }
}
