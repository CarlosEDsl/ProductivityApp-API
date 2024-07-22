package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.models.enums.ProfileEnum;
import com.eduardocarlos.productivityApp.repositories.UserRepository;

import com.eduardocarlos.productivityApp.security.UserDetailsImpl;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    //Searches
    public User findById(Long id) {
    UserDetailsImpl user = authenticated();
        if(Objects.isNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), id)){
                throw new RuntimeException("UNAUTHORIZED USER SEARCHING FOR ANOTHER USER");
        }
        return this.userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                User.class, "User not found id: " + id
        ));

    }

    public User findByEmail(String email) {
        UserDetailsImpl userDetails = authenticated();
        if(Objects.isNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN) && !Objects.equals(userDetails.getUser().getEmail(), email)){
            throw new RuntimeException("UNAUTHORIZED USER SEARCHING FOR ANOTHER USER");
        }
        Optional<User> user = this.userRepository.findByEmail(email);
        return user
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public List<User> findAll() {
        UserDetailsImpl userDetails = authenticated();
        if(Objects.isNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN)){
            throw new RuntimeException("UNAUTHORIZED USER SEARCHING FOR ANOTHER USER");
        }
        List<User> users = this.userRepository.findAll();
        return users;
    }

    //Create
    @Transactional
    public User create(User user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProfiles(Stream.of(ProfileEnum.DEFAULT.getCode()).collect(Collectors.toSet()));

        return userRepository.save(user);
    }

    //Update
    @Transactional
    public User update(User user) {
        UserDetailsImpl userDetails = authenticated();
        if(Objects.isNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN) && !Objects.equals(userDetails.getUser().getId(), user.getId())){
            throw new RuntimeException("UNAUTHORIZED USER SEARCHING FOR ANOTHER USER");
        }
        User updatedUser = this.findById(user.getId());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setName(user.getName());
        updatedUser.setCell(user.getCell());
        updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return this.userRepository.save(updatedUser);
    }

    //Delete
    public void delete(Long id, String email) {
        UserDetailsImpl userDetails = authenticated();
        if(Objects.isNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN) &&
                !Objects.equals(userDetails.getUser().getEmail(), email) || !Objects.equals(userDetails.getUser().getId(), id)){
            throw new RuntimeException("UNAUTHORIZED USER SEARCHING FOR ANOTHER USER");
        }
        User user = this.findByEmail(email);
        if(!user.equals(this.findById(id))) {
            throw new RuntimeException("User and ID dont match");
        }

        try {
            this.userRepository.delete(this.findById(id));
        } catch (Exception e) {
            throw new RuntimeException("User have relationships in database");
        }
    }

    //Security
    public static UserDetailsImpl authenticated(){
        try{
            return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
