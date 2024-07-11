package com.eduardocarlos.productivityApp.repositories;

import com.eduardocarlos.productivityApp.models.MonthStatistic;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MonthStatisticRepository extends JpaRepository<MonthStatistic, Long> {

    List<MonthStatistic> findAllByUser_Id(Long id);

    MonthStatistic findByUser_IdAndMonth(Long user_id, @NotBlank LocalDate month);
}
