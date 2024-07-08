package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.repositories.TaskRepository;
import com.eduardocarlos.productivityApp.repositories.UserRepository;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    TaskRepository taskRepository;
    UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<Task> findAllByUser(Long id) {
        List<Task> tasks = this.taskRepository.findAllByUser_Id(id);
        return tasks;
    }

    public Task create(Task task) {

        User user = this.userService.findById(task.getUser().getId());
        task.setId(null);
        task.setUser(user);
        return this.taskRepository.save(task);
    }

    public Task update(Task task) {
        Optional<Task> updatedTask = this.taskRepository.findById(task.getId());
        if(updatedTask.isEmpty())
            throw new RuntimeException("task not found");


        updatedTask.ifPresent(taskToUpdate -> {
            if (Objects.nonNull(task.getName())) {
                taskToUpdate.setName(task.getName());
            }
            if (Objects.nonNull(task.getDescription())) {
                taskToUpdate.setDescription(task.getDescription());
            }
            if (Objects.nonNull(task.getTerm())) {
                taskToUpdate.setTerm(task.getTerm());
            }
            if (Objects.nonNull(task.getFinishDate())) {
                taskToUpdate.setFinishDate(task.getFinishDate());
            }
        });

        return this.taskRepository.save(updatedTask.get());

    }

    public void delete(Long id) {
        try{
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
