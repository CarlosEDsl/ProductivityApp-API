package com.eduardocarlos.productivityApp.controller;

import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.services.TaskService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/task")
@Validated
public class TaskController {

    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        try {
            Task task = this.taskService.findById(id);
            return ResponseEntity.ok().body(task);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<Task>> findAllByUser(@PathVariable Long user_id) {
        List<Task> tasks = this.taskService.findAllByUser(user_id);
        return ResponseEntity.ok().body(tasks);
    }

    @PostMapping()
    public ResponseEntity<Task> create(@Valid @RequestBody Task task) {
        this.taskService.create(task);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody Task task){
        task.setId(id);
        return ResponseEntity.ok().body(this.taskService.update(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
