package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.repositories.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        Optional<User> user = this.userRepository.findById(id);
        return user
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public User findByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        return user
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public List<User> findAll() {
        List<User> users = this.userRepository.findAll();
        return users;
    }

    //Create
    @Transactional
    public User create(User user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    //Update
    @Transactional
    public User update(User user) {
        User updatedUser = this.findById(user.getId());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setName(user.getName());
        updatedUser.setCell(user.getCell());
        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        return this.userRepository.save(updatedUser);
    }

    //Delete
    public void delete(Long id, String email) {
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

}
