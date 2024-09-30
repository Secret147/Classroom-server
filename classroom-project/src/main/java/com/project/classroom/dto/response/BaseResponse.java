package com.project.classroom.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse {
    private String status;

    private String error;

    private Object data;

    private String message;
}
