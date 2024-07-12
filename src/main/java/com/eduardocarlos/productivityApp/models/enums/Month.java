package com.eduardocarlos.productivityApp.models.enums;

public enum Month {
    JAN(1), FEB(2), MARCH(3), APRIL(4), MAY(5), JUN(6), JULY(7), AUG(8), SEPT(9), OCT(10), NOV(11), DEC(12);

    private final int month;
    Month(int month) {
        this.month = month;
    }

    public int getValue(){
        return this.month;
    }

    public static Month fromValue(int value) {
        for (Month month : Month.values()) {
            if (month.getValue() == value) {
                return month;
            }
        }
        throw new IllegalArgumentException("Unknown month value: " + value);
    }

}
