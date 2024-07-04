package com.eduardocarlos.productivityApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = MonthStatistic.table_name)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonthStatistic {

    public static final String table_name = "monthstatistic";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @NotBlank
    private String month;

    @Column
    private Float avgConclusions;

    @Column
    private Float avgHoursPDay;

}
