package org.project.todayclothes.global;

public enum Feedback {
    PERFECT,
    HOT,
    COLD;

    public static boolean isValid(String value) {
        for (Feedback feedback : Feedback.values()) {
            if (feedback.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
