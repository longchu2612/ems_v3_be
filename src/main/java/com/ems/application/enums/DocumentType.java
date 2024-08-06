package com.ems.application.enums;

public enum DocumentType {
    AVATAR(1),
    DELIVERY_ORDER_CHECK_IN_IMAGE(2),
    DELIVERY_ORDER_CHECK_IN_SIGNATURE(3),
    DELIVERY_ORDER_LOADING_IMAGE(4),
    DELIVERY_ORDER_LOADING_SIGNATURE(5),
    DELIVERY_ORDER_LOADING_COMPLETE_IMAGE(6),
    DELIVERY_ORDER_LOADING_COMPLETE_SIGNATURE(7),
    DELIVERY_ORDER_DELIVERY_FINISH_IMAGE(8),
    DELIVERY_ORDER_DELIVERY_FINISH_SIGNATURE(9),
    DELIVERY_ORDER_DELIVERY_REJECT_IMAGE(10),
    DELIVERY_ORDER_DELIVERY_REJECT_SIGNATURE(11);

    private final int value;

    DocumentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
