package com.project.classroom.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SearchResDto {
    private int limit;

    private int offset;

    private Object data;

    private long total;
}
