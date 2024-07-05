package com.example.jobTpro.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobStatus {
    PENDING("pending"),
    INTERVIEW("interview"),
    DECLINED("declined");

    private final String value;

    JobStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JobStatus fromValue(String value) {
        for (JobStatus status : JobStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value);
    }
}
