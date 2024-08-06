package com.ems.application.enums;

public enum DeviceType {
    ANDROID(1),
    IOS(2);

    private final int value;

    DeviceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
