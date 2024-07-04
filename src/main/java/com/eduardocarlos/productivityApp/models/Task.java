package com.eduardocarlos.productivityApp.models;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = Task.table_name)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {

    public static final String table_name = "tasks";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false, length = 20)
    @Size(max = 20)
    @NotBlank
    private String name;

    @Column(length = 250)
    private String description;

    @Column(nullable = false)
    private LocalDateTime inputDate = LocalDateTime.now();

    @Column
    private LocalDateTime finishDate;

    @Column(nullable = false)
    @NotBlank
    private LocalDateTime term;

}
