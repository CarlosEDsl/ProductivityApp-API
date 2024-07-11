package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.repositories.MonthStatisticRepository;
import org.springframework.stereotype.Service;

@Service
public class MonthStatisticService {

    private final MonthStatisticRepository monthStatisticRepository;

    public MonthStatisticService(MonthStatisticRepository monthStatisticRepository){
        this.monthStatisticRepository = monthStatisticRepository;
    }

    public MonthStatistic create(MonthStatistic monthStatistic){
        monthStatistic.setId(null);



        return this.monthStatisticRepository.save(monthStatistic);
    }

}
