package io.fptu.sep490.constant;

import lombok.Getter;

@Getter
public enum ActivityStatus {
    INACTIVE(false),
    ACTIVE(true),
    LOCKED(true),
    UNLOCKED(false);

    private final boolean status;

    ActivityStatus(final boolean status) {this. status = status;}
}