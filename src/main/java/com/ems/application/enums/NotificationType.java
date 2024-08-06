package com.ems.application.enums;

public enum NotificationType {
    ASSIGN_JOB(1);

    private final int value;

    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
