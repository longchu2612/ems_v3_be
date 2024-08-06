package com.ems.application.enums;

public enum OrderDetailStatus {
    PENDING(1),
    PROCESSING(2),
    SERVING(3),
    SERVED(4),
    REJECTED(5);

    private final int value;

    OrderDetailStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
