package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.models.enums.Month;
import com.eduardocarlos.productivityApp.models.enums.ProfileEnum;
import com.eduardocarlos.productivityApp.repositories.MonthStatisticRepository;
import com.eduardocarlos.productivityApp.repositories.TaskRepository;
import com.eduardocarlos.productivityApp.security.UserDetailsImpl;
import com.eduardocarlos.productivityApp.utils.DateFormater;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public MonthStatistic create(User user, LocalDateTime firstDate){
        MonthStatistic monthStatistic = new MonthStatistic();

        monthStatistic.setUser(user);

        //Creating in first task creation
        monthStatistic.setYear(firstDate.toLocalDate().getYear());

        //Using LocalDate to create the month enum
        Month month = Month.fromValue(firstDate.getMonthValue());
        monthStatistic.setMonth(month);

        //Creating metrics with de amount of tasks in user tasks
        monthStatistic.setTotalHours(new BigDecimal(0));
        monthStatistic.setAvgConclusions(this.avgConclusionsCalculate(this.taskRepository.findAllByUser_Id(user.getId())));

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
    @Transactional
    public void update(User user, LocalDateTime term) {

        Optional<MonthStatistic> updatedStatisticsOp = this.monthStatisticRepository
                .findByUser_IdAndMonthAndYear(user.getId(), DateFormater.DateTimeToMonthEnum(term), term.getYear());

        updatedStatisticsOp.ifPresent(statistic -> {

            List<Task> tasks = this.taskRepository.findAllByUser_Id(statistic.getUser().getId());
            statistic.setAvgConclusions(this.avgConclusionsCalculate(tasks));

            this.monthStatisticRepository.save(statistic);

        });
    }

    @Transactional
    public MonthStatistic addHoursToMonth(User user, Integer month, Integer year, BigDecimal hours){

        UserDetailsImpl userDetails = UserService.authenticated();
        if(!Objects.nonNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN) && !Objects.equals(userDetails.getUser().getId(), user.getId())){
            throw new RuntimeException("UNAUTHORIZED");
        }

        Month monthEnum = Month.fromValue(month);
        MonthStatistic monthS = this.findByUserAndDate(user, monthEnum, year);
        if(Objects.nonNull(monthS.getTotalHours())){
            monthS.setTotalHours(monthS.getTotalHours().add(hours));
        } else{
            monthS.setTotalHours(hours);
        }

        return monthS;
    }

    public Optional<MonthStatistic> findByMonth(Long user_id, Month month, Integer year) {
        return this.monthStatisticRepository.findByUser_IdAndMonthAndYear(user_id, month, year);
    }

    public MonthStatistic findById(Long id){

        Optional<MonthStatistic> monthStatistic = this.monthStatisticRepository.findById(id);

        if(monthStatistic.isEmpty()){
            throw new RuntimeException("MONTH NOT FOUND");
        }

        UserDetailsImpl user = UserService.authenticated();
        if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), monthStatistic.get().getUser().getId())){
            throw new RuntimeException("UNAUTHORIZED");
        }

        return monthStatistic.get();
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
