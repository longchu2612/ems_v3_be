package com.ems.application.enums;

public enum NotificationStatus {
    UNREAD(1),
    READ(2);

    private final int value;

    NotificationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return (short) value;
    }
}
