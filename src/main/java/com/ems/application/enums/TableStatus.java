package com.ems.application.enums;

public enum TableStatus {
    UNSUPPORTED(0), // Không hiển thị
    EMPTY(1), // Trống
    PENDING(2), // Chờ phục vụ
    SERVED(3), // Đã phục vụ hết
    DONE(4), // Đợi thanh toán
    WAIT(5);//D ọn bàn đón khách mới

    private final int value;

    TableStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
