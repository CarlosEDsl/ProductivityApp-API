package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.repositories.UserRepository;

import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    //Searches
    public User findById(UUID id) {
        Optional<User> user = this.userRepository.findById(id);
        return user
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public User findByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        return user
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public User findByUsername(String username) {
        Optional<User> user = this.userRepository.findByName(username);
        return user
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public List<User> findAll() {
        List<User> users = this.userRepository.findAll();
        return users;
    }

    //Create
    public User create(User user) {
        return userRepository.save(user);
    }

    //Update
    public User update(User user) {
        User updatedUser = this.findById(user.getId());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setName(user.getName());
        updatedUser.setCell(user.getCell());
        updatedUser.setPassword(user.getPassword());

        return this.userRepository.save(updatedUser);
    }

    //Delete
    public void delete(UUID id, String email) {
        User user = this.findByEmail(email);
        if(!user.equals(this.findById(id))) {
            throw new RuntimeException("User and ID dont match");
        }

        try {
            this.userRepository.delete(this.findById(id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
