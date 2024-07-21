package com.eduardocarlos.productivityApp.controller;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.models.dtos.AddHoursDTO;
import com.eduardocarlos.productivityApp.models.dtos.DateStatisticDTO;

import com.eduardocarlos.productivityApp.services.MonthStatisticService;
import com.eduardocarlos.productivityApp.services.UserService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@Validated
public class UserController{

    private final UserService userService;
    private final MonthStatisticService monthStatisticService;

    public UserController(UserService userService, MonthStatisticService monthStatisticService) {
        this.userService = userService;
        this.monthStatisticService = monthStatisticService;
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
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = this.userService.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/statistic/{id}")
    public ResponseEntity<MonthStatistic> findMonthStatistic(@PathVariable Long id, @RequestBody DateStatisticDTO date){
        MonthStatistic statistic = this.monthStatisticService.findByUserAndDate
                                                                (userService.findById(id), date.getMonth(), date.getYear());
        return ResponseEntity.ok(statistic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user){
        user.setId(id);
        User updatedUser = this.userService.update(user);
        return ResponseEntity.ok().body(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        this.userService.delete(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<MonthStatistic> addHoursToMonth(@PathVariable Long user_id, @RequestBody AddHoursDTO addHours) {
        if(Objects.nonNull(addHours) && addHours.hours() > 0) {
            BigDecimal hours = new BigDecimal(addHours.hours());
            User user = this.userService.findById(user_id);
            return ResponseEntity.ok().body(this.monthStatisticService.addHoursToMonth(user, addHours.month(), addHours.year(), hours));
        }
        return ResponseEntity.unprocessableEntity().build();

    }

}
