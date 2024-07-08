package com.eduardocarlos.productivityApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = User.table_name)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    public static final String table_name = "users";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false, length = 256)
    @NotBlank
    private String email;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(min=8, max = 100)
    private String password;

    @Column(nullable = false, length = 30)
    @NotBlank
    private String name;

    @Column(length = 15)
    @Size(max=15)
    private String cell;

}
