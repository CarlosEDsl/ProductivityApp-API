package com.eduardocarlos.productivityApp.controller;

import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.services.UserService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Validated
public class UserController{

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Just for test
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = this.userService.findAll();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody User user) {
        User newUser = this.userService.create(user);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable UUID id) {
        User user = this.userService.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable UUID id, @Valid @RequestBody User user){
        user.setId(id);
        User updatedUser = this.userService.update(user);
        return ResponseEntity.ok().body(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        this.userService.delete(id, email);
        return ResponseEntity.noContent().build();
    }




}
