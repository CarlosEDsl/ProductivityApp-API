package com.eduardocarlos.productivityApp.models.dtos;

import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class TaskDTO {

    @NotNull
    private Long user_id;

    @NotBlank
    @Size(max=20)
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime term;
    private LocalDateTime finishDate;

    public Task fromDTO(){
        Task task = new Task();

        User user = new User();
        user.setId(this.user_id);

        task.setName(this.name);
        task.setUser(user);
        task.setDescription(this.description);
        task.setTerm(this.term);
        task.setFinishDate(this.finishDate);

        return task;
    }
}
