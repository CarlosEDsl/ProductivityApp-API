package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.models.User;

import com.eduardocarlos.productivityApp.models.enums.ProfileEnum;
import com.eduardocarlos.productivityApp.repositories.TaskRepository;
import com.eduardocarlos.productivityApp.security.UserDetailsImpl;
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
            throw new RuntimeException("UNAUTHORIZED");
        }
        return this.taskRepository.findAllByUser_Id(id);
    }

    public Task findById(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Task> task = this.taskRepository.findById(id);
        if(task.isEmpty()){
            throw new RuntimeException("TASK NOT FOUND");
        }
        UserDetailsImpl user = UserService.authenticated();
        if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), task.get().getUser().getId())){
            throw new RuntimeException("UNAUTHORIZED");
        }

        return task.orElseThrow(ChangeSetPersister.NotFoundException::new);
    }


    //CREATE
    @Transactional
    public Task create(Task task) {

        User user = this.userService.findById(task.getUser().getId());
        UserDetailsImpl userDetails = UserService.authenticated();
        if(!Objects.nonNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN) && !Objects.equals(userDetails.getUser().getId(), user.getId())){
            throw new RuntimeException("UNAUTHORIZED");
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
            throw new RuntimeException("UNAUTHORIZED");
        }

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

                //Updating Statistics
                if(this.monthVerifier(task.getTerm(), task.getUser().getId()))
                    this.monthStatisticService.create(taskToUpdate.getUser(), taskToUpdate.getTerm());
            }
            if (Objects.nonNull(task.getFinishDate())) {
                taskToUpdate.setFinishDate(task.getFinishDate());
                this.monthStatisticService.update(taskToUpdate.getUser(), taskToUpdate.getTerm());
            }
        });

        return this.taskRepository.save(updatedTask.get());

    }


    //DELETE
    public void delete(Long id) {
        try{
            Optional<Task> taskOp = this.taskRepository.findById(id);

            if(taskOp.isEmpty()){
                throw new RuntimeException("Task not found");
            }

            UserDetailsImpl user = UserService.authenticated();
            if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), taskOp.get().getUser().getId())){
                throw new RuntimeException("UNAUTHORIZED");
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
