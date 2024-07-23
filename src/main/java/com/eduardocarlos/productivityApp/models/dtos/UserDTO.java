package com.eduardocarlos.productivityApp.models.dtos;

import com.eduardocarlos.productivityApp.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    @NotBlank
    @Size(max = 256)
    private String email;

    @NotBlank
    @Size(min=8, max=30)
    private String password;

    @NotBlank
    @Size(max=30)
    private String name;

    @Size(max=15)
    private String cell;

    public User fromDTO() {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setCell(cell);
        return user;
    }

}
