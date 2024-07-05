package com.example.jobTpro.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobType {
    FULL_TIME("full-time"),
    PART_TIME("part-time"),
    INTERNSHIP("internship"),
    REMOTE("remote");

    private final String value;

    JobType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JobType fromValue(String value) {
        for (JobType jobType : JobType.values()) {
            if (jobType.value.equalsIgnoreCase(value)) {
                return jobType;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value);
    }
}
