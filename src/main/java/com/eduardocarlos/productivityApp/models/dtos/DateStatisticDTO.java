package com.eduardocarlos.productivityApp.models.dtos;

import com.eduardocarlos.productivityApp.models.enums.Month;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
public class DateStatisticDTO {
    private Integer month;
    private Integer year;

    public Month getMonth() {
        return Month.fromValue(this.month);
    }

}
