package com.vcube.academy.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private int                 status;
    private String              error;
    private String              message;
    private Map<String, String> fieldErrors;
    private Instant             timestamp;
}
