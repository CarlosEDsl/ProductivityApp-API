package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.repositories.UserRepository;

import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

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
                .orElseThrow(()-> new ObjectNotFoundException(id, "User not found"));
    }

    public User findByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        return user
                .orElseThrow(()-> new ObjectNotFoundException(user, "User not found"));
    }

    public User findByUsername(String username) {
        Optional<User> user = this.userRepository.findByName(username);
        return user
                .orElseThrow(()-> new ObjectNotFoundException(user, "User not found"));
    }

    //Create
    public User create(User user) {
        user.setId(null);
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
        this.findById(id);
        this.findByEmail(email);

        try {
            this.userRepository.delete(this.findById(id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
