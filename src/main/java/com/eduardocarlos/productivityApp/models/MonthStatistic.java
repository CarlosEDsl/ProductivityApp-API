package com.eduardocarlos.productivityApp.models;

import com.eduardocarlos.productivityApp.models.enums.Month;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Month month;

    @Column(nullable = false)
    @NotNull
    private Integer year;

    @Column
    private BigDecimal avgConclusions;

    @Column
    private BigDecimal totalHours;

}
