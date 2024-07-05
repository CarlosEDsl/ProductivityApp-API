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
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Validated
public class UserController{

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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


    //Just for test
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = this.userService.findAll();
        return ResponseEntity.ok().body(users);
    }



}
