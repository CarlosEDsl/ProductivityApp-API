package com.eduardocarlos.productivityApp.models;

import com.eduardocarlos.productivityApp.models.enums.Month;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = MonthStatistic.table_name)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonthStatistic {

    public static final String table_name = "monthstatistics";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @NotBlank
    private Month month;

    @Column(nullable = false)
    @NotBlank
    private LocalDate date;

    @Column
    private BigDecimal avgConclusions;

    @Column
    private BigDecimal avgHoursPDay;

}
