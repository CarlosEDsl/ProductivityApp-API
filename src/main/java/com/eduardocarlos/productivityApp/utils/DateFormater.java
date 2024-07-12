package com.eduardocarlos.productivityApp.utils;

import com.eduardocarlos.productivityApp.models.enums.Month;

import java.time.LocalDateTime;

public class DateFormater {

    public static Month DateTimeToMonthEnum(LocalDateTime date){
        return Month.fromValue(date.getMonthValue());
    }



}
