package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.models.enums.Month;
import com.eduardocarlos.productivityApp.repositories.MonthStatisticRepository;
import com.eduardocarlos.productivityApp.repositories.TaskRepository;
import com.eduardocarlos.productivityApp.utils.DateFormater;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MonthStatisticService {

    private final MonthStatisticRepository monthStatisticRepository;
    private final TaskRepository taskRepository;

    public MonthStatisticService(MonthStatisticRepository monthStatisticRepository, TaskRepository taskRepository){
        this.monthStatisticRepository = monthStatisticRepository;
        this.taskRepository = taskRepository;
    }

    public MonthStatistic create(User user, LocalDateTime firstDate){
        MonthStatistic monthStatistic = new MonthStatistic();

        monthStatistic.setUser(user);

        //Creating in first task creation
        monthStatistic.setYear(firstDate.toLocalDate().getYear());

        //Using LocalDate to create the month enum
        Month month = Month.fromValue(firstDate.getMonthValue());
        monthStatistic.setMonth(month);

        //Creating metrics with de amount of tasks in user tasks
        monthStatistic.setAvgHoursPDay(this.avgConclusionsCalculate(this.taskRepository.findAllByUser_Id(user.getId())));
        monthStatistic.setAvgConclusions(new BigDecimal(0));

        return this.monthStatisticRepository.save(monthStatistic);
    }

    public MonthStatistic findByUserAndDate(User user, Month month, Integer year) {
        Optional<MonthStatistic> monthStatistic = this.monthStatisticRepository
                                                    .findByUser_IdAndMonthAndYear(user.getId(), month, year);
        if(monthStatistic.isPresent()){
            return monthStatistic.get();
        }
        throw new RuntimeException();
    }

    //Update will just change the metrics of one month
    public void update(User user, LocalDateTime term) {

        Optional<MonthStatistic> updatedStatisticsOp = this.monthStatisticRepository
                .findByUser_IdAndMonthAndYear(user.getId(), DateFormater.DateTimeToMonthEnum(term), term.getYear());

        updatedStatisticsOp.ifPresent(statistic -> {

            List<Task> tasks = this.taskRepository.findAllByUser_Id(statistic.getUser().getId());
            statistic.setAvgConclusions(this.avgConclusionsCalculate(tasks));

            this.monthStatisticRepository.save(statistic);

        });
    }

    public Optional<MonthStatistic> findByMonth(Long user_id, Month month, Integer year) {
        return this.monthStatisticRepository.findByUser_IdAndMonthAndYear(user_id, month, year);
    }

    //Method to calculate the percentage of tasks finished
    private BigDecimal avgConclusionsCalculate(List<Task> tasks) {
        List<Task> completedTasks = tasks.stream()
                .filter(task -> Objects.nonNull(task.getFinishDate()))
                .toList();
        BigDecimal percConclusion;

        //Handling 0 tasks
        try{
            percConclusion = new BigDecimal(completedTasks.size()).divide(new BigDecimal(tasks.size()),
                    new MathContext(5));
        } catch (ArithmeticException e) {
            percConclusion = BigDecimal.ZERO;
        }

        return percConclusion;
    }
}
