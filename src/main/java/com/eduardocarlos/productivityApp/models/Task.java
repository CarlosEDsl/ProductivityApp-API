package com.eduardocarlos.productivityApp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = Task.table_name)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {

    public static final String table_name = "tasks";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    private LocalDateTime inputDate = LocalDateTime.parse(LocalDateTime.now()
            .format(DateTimeFormatter
                    .ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

    @Column
    private LocalDateTime finishDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime term;

}
