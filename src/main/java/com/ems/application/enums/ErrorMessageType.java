package com.ems.application.enums;

public enum ErrorMessageType {
    NORMAL(""),
    SHOW_IN_MESSAGE("ShowInMessage"),
    SHOW_IN_TITLE("ShowInTitle"),
    GLOBALOBJINOBJ("GlobalObjectInObject"),
    PRIVATEOBJINOBJ("PrivateObjectInObject");

    private final String value;

    ErrorMessageType(String value) {
        this.value = value;
    }

    public final String getValue() {
        return this.value;
    }
}
