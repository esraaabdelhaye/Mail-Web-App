package com.mailapp.mailbackend.enums;


public enum Priority {
    URGENT(4),
    HIGH(3),
    NORMAL(2),
    LOW(1);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {return value;}

    public static Priority fromValue(int value) {
        for (Priority p : Priority.values()) {
            if (p.value == value) return p;
        }
        throw new IllegalArgumentException("Invalid priority value: " + value);
    }







}
