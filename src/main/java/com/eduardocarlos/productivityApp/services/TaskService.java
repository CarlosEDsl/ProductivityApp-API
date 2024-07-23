package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.models.User;

import com.eduardocarlos.productivityApp.models.enums.ProfileEnum;
import com.eduardocarlos.productivityApp.repositories.TaskRepository;
import com.eduardocarlos.productivityApp.security.UserDetailsImpl;
import com.eduardocarlos.productivityApp.services.exceptions.ObjectNotFoundException;
import com.eduardocarlos.productivityApp.services.exceptions.UnauthorizedUserException;
import com.eduardocarlos.productivityApp.utils.DateFormater;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    TaskRepository taskRepository;
    UserService userService;
    //TaskService is linked with monthStatistic to update in real time when a task is completed or added
    MonthStatisticService monthStatisticService;

    public TaskService(TaskRepository taskRepository, UserService userService, MonthStatisticService monthStatisticService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.monthStatisticService = monthStatisticService;
    }

    //FIND
    public List<Task> findAllByUser(Long id) {
        UserDetailsImpl user = UserService.authenticated();
        if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), id)){
            throw new UnauthorizedUserException("trying to access unauthorized list of tasks");
        }
        return this.taskRepository.findAllByUser_Id(id);
    }

    public Task findById(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Task> task = this.taskRepository.findById(id);
        if(task.isEmpty()){
            throw new ObjectNotFoundException("TASK NOT FOUND ID: " + id);
        }
        UserDetailsImpl user = UserService.authenticated();
        if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), task.get().getUser().getId())){
            throw new UnauthorizedUserException("trying to see another user task");
        }
        return task.get();
    }


    //CREATE
    @Transactional
    public Task create(Task task) {

        User user = this.userService.findById(task.getUser().getId());
        UserDetailsImpl userDetails = UserService.authenticated();
        if(!Objects.nonNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN) && !Objects.equals(userDetails.getUser().getId(), user.getId())){
            throw new UnauthorizedUserException("trying create a task for another user");
        }
        task.setId(null);
        task.setUser(user);

        this.taskRepository.save(task);

        if(!monthVerifier(task.getTerm(), task.getUser().getId()))
            this.monthStatisticService.create(task.getUser(), task.getTerm());
        else
            this.monthStatisticService.update(task.getUser(), task.getTerm());

        return task;
    }


    //UPDATE
    @Transactional
    public Task update(Task task) {
        Optional<Task> updatedTask = this.taskRepository.findById(task.getId());

        UserDetailsImpl user = UserService.authenticated();
        if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), task.getUser().getId())){
            throw new UnauthorizedUserException("trying to update task from another user");
        }

        if(updatedTask.isEmpty())
            throw new ObjectNotFoundException("task not found for update");

        updatedTask.ifPresent(taskToUpdate -> {
            if (Objects.nonNull(task.getName())) {
                taskToUpdate.setName(task.getName());
            }
            if (Objects.nonNull(task.getDescription())) {
                taskToUpdate.setDescription(task.getDescription());
            }
            if (Objects.nonNull(task.getTerm())) {
                taskToUpdate.setTerm(task.getTerm());

                //Updating Statistics
                if(this.monthVerifier(task.getTerm(), task.getUser().getId()))
                    this.monthStatisticService.create(taskToUpdate.getUser(), taskToUpdate.getTerm());
            }
            taskToUpdate.setFinishDate(task.getFinishDate());
            this.monthStatisticService.update(taskToUpdate.getUser(), taskToUpdate.getTerm());
        });

        return this.taskRepository.save(updatedTask.get());

    }


    //DELETE
    public void delete(Long id) {
        try{
            Optional<Task> taskOp = this.taskRepository.findById(id);

            if(taskOp.isEmpty()){
                throw new ObjectNotFoundException("Task not found for delete");
            }

            UserDetailsImpl user = UserService.authenticated();
            if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), taskOp.get().getUser().getId())){
                throw new UnauthorizedUserException("trying to delete a task from another user");
            }

            this.taskRepository.deleteById(id);
            taskOp.ifPresent(task -> this.monthStatisticService.update(task.getUser(), task.getTerm()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //EXTRA
    private boolean monthVerifier(LocalDateTime date, Long user_id){
        Optional<MonthStatistic> statistic = monthStatisticService.findByMonth(user_id, DateFormater.DateTimeToMonthEnum(date), date.getYear());

        return statistic.filter(monthStatistic -> (monthStatistic.getYear() == date.toLocalDate().getYear())).isPresent();
    }

}
