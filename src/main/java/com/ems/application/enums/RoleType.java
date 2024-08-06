package com.ems.application.enums;

public enum RoleType {

    ROLE_ADMIN(1, "role_admin", "admin"),
    ROLE_MANAGER(2, "role_manager", "manager"),
    ROLE_CHEF(3, "role_chef", "chef"),
    ROLE_WAITER(4, "role_waiter", "waiter"),
    ROLE_CASHIER(5, "role_cashier", "cashier");

    private final int key;
    private final String value;
    private final String role;

    RoleType(int key, String value, String role) {
        this.key = key;
        this.value = value;
        this.role = role;
    }

    public String getValue() {
        return value;
    }

    public String getRole() {
        return role;
    }

    public int getKey() {
        return key;
    }
}
