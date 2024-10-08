package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.MonthStatistic;
import com.eduardocarlos.productivityApp.models.Task;
import com.eduardocarlos.productivityApp.models.User;
import com.eduardocarlos.productivityApp.models.dtos.MonthCurrentData;
import com.eduardocarlos.productivityApp.models.enums.Month;
import com.eduardocarlos.productivityApp.models.enums.ProfileEnum;
import com.eduardocarlos.productivityApp.repositories.MonthStatisticRepository;
import com.eduardocarlos.productivityApp.repositories.TaskRepository;
import com.eduardocarlos.productivityApp.security.UserDetailsImpl;
import com.eduardocarlos.productivityApp.services.exceptions.ObjectNotFoundException;
import com.eduardocarlos.productivityApp.services.exceptions.UnauthorizedUserException;
import com.eduardocarlos.productivityApp.utils.DateFormater;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
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
        List<Task> tasks = this.taskRepository.findAllByUser_Id(user.getId()).stream()
                .filter(task -> task.getTerm().getMonthValue() == month.getValue()).toList();
        monthStatistic.setAvgConclusions(this.avgConclusionsCalculate(tasks));

        return this.monthStatisticRepository.save(monthStatistic);
    }

    public MonthStatistic findByUserAndDate(User user, Month month, Integer year) {
        Optional<MonthStatistic> monthStatistic = this.monthStatisticRepository
                                                    .findByUser_IdAndMonthAndYear(user.getId(), month, year);
        if(monthStatistic.isPresent()){
            return monthStatistic.get();
        }
        throw new ObjectNotFoundException(MonthStatistic.class);
    }

    //Update will just change the metrics of one month
    @Transactional
    public MonthStatistic update(User user, LocalDateTime term) {
        Optional<MonthStatistic> updatedStatisticsOp = this.monthStatisticRepository
                .findByUser_IdAndMonthAndYear(user.getId(), DateFormater.DateTimeToMonthEnum(term), term.getYear());

        if(updatedStatisticsOp.isPresent()){
            List<Task> tasks = this.taskRepository.findAllByUser_Id(user.getId()).stream()
                    .filter(task -> task.getTerm().getMonthValue() == term.getMonthValue()).toList();
            updatedStatisticsOp.get().setAvgConclusions(this.avgConclusionsCalculate(tasks));

        }

        return this.monthStatisticRepository.save(updatedStatisticsOp.get());
    }

    @Transactional
    public MonthStatistic addHoursToMonth(User user, Integer month, Integer year, BigDecimal hours){

        UserDetailsImpl userDetails = UserService.authenticated();
        if(!Objects.nonNull(userDetails) || !userDetails.hasRole(ProfileEnum.ADMIN) && !Objects.equals(userDetails.getUser().getId(), user.getId())){
            throw new UnauthorizedUserException("trying to add hours to another user");
        }

        Month monthEnum = Month.fromValue(month);
        Optional<MonthStatistic> monthS = this.monthStatisticRepository.findByUser_IdAndMonthAndYear(user.getId(), monthEnum, year);
        MonthStatistic monthStatistic;

        if(monthS.isEmpty()){
            LocalDateTime date = LocalDateTime.of(year, month, 1, 0, 0);
            monthStatistic = this.create(user, date);
        } else {
            monthStatistic = monthS.get();
        }

        if(Objects.nonNull(monthStatistic.getTotalHours())){
            monthStatistic.setTotalHours(monthStatistic.getTotalHours().add(hours));
        } else{
            monthStatistic.setTotalHours(hours);
        }

        return monthStatistic;
    }

    public Optional<MonthStatistic> findByMonth(Long user_id, Month month, Integer year) {
        return this.monthStatisticRepository.findByUser_IdAndMonthAndYear(user_id, month, year);
    }

    public MonthStatistic findById(Long id){

        Optional<MonthStatistic> monthStatistic = this.monthStatisticRepository.findById(id);

        if(monthStatistic.isEmpty()){
            throw new ObjectNotFoundException(MonthStatistic.class);
        }

        UserDetailsImpl user = UserService.authenticated();
        if(!Objects.nonNull(user) || !user.hasRole(ProfileEnum.ADMIN) && !Objects.equals(user.getUser().getId(), monthStatistic.get().getUser().getId())){
            throw new UnauthorizedUserException("trying to get the month statistic from another user");
        }

        return monthStatistic.get();
    }

    public MonthCurrentData getMonthCompleteMetrics(Long id) {
        Date today = new Date();
        BigDecimal finished = BigDecimal.valueOf(0);
        BigDecimal notFinished = BigDecimal.valueOf(0);
        BigDecimal overdue = BigDecimal.valueOf(0);
        List<Task> tasks = this.taskRepository.findAllByUser_Id(id)
                .stream().filter(task -> task.getTerm().getMonth().getValue() == today.getMonth()+1).toList();

        if(!tasks.isEmpty()) {

            for (Task task : tasks) {
                LocalDateTime taskTerm = task.getTerm();
                Date taskTermDate = Date.from(taskTerm.atZone(ZoneId.systemDefault()).toInstant());
                
                if (task.getFinishDate() == null && today.after(taskTermDate)){
                    overdue = overdue.add(BigDecimal.valueOf(1));
                } else if (task.getFinishDate() == null) {
                    notFinished = notFinished.add(BigDecimal.valueOf(1));
                } else {
                    finished = finished.add(BigDecimal.valueOf(1));
                }
            }
        }

        return new MonthCurrentData(finished, overdue, notFinished);

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
