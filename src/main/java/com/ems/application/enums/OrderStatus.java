package com.ems.application.enums;

public enum OrderStatus {
    EMPTY(1),
    IN_PROGRESS(2),
    SERVE_DONE(3),
    DONE(4),
    CLOSE_BANKING(5),
    CLOSE_CASH(6);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
