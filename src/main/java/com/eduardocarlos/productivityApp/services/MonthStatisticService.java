package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.models.enums.Month;
import com.eduardocarlos.productivityApp.repositories.MonthStatisticRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MonthStatisticService {

    private final MonthStatisticRepository monthStatisticRepository;

    public MonthStatisticService(MonthStatisticRepository monthStatisticRepository){
        this.monthStatisticRepository = monthStatisticRepository;
    }

    public MonthStatistic create(User user){
        MonthStatistic monthStatistic = new MonthStatistic();

        monthStatistic.setUser(user);
        monthStatistic.setDate(LocalDate.now());

        Month month = Month.fromValue(monthStatistic.getDate().getMonthValue());

        monthStatistic.setMonth(month);
        monthStatistic.setAvgHoursPDay(new BigDecimal(0));
        monthStatistic.setAvgConclusions(new BigDecimal(0));

        return this.monthStatisticRepository.save(monthStatistic);
    }

    public MonthStatistic update(MonthStatistic monthStatistic) {
        MonthStatistic reStatisticGet = monthStatisticRepository.findById(monthStatistic.getId())
                .orElseThrow();

        return this.monthStatisticRepository.save(monthStatistic);
    }

}
