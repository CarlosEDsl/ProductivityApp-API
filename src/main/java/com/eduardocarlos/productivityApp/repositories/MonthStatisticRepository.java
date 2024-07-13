package com.eduardocarlos.productivityApp.repositories;

import com.eduardocarlos.productivityApp.models.MonthStatistic;

import com.eduardocarlos.productivityApp.models.enums.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthStatisticRepository extends JpaRepository<MonthStatistic, Long> {

    Optional<List<MonthStatistic>> findAllByUser_Id(Long id);

    Optional<MonthStatistic> findByUser_IdAndMonthAndYear(Long user_id, Month month, Integer year);
}
