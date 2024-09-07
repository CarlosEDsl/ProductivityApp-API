package com.eduardocarlos.productivityApp.models.dtos;

import java.math.BigDecimal;

public record MonthCurrentData(BigDecimal finished, BigDecimal overdue, BigDecimal notFinished) {
}
