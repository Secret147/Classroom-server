package com.project.classroom.dto.response;

import lombok.Data;

@Data
public class ListResponseDto<T> {
    private long limit;

    private long offset;

    private long total;

    private T data;
}
