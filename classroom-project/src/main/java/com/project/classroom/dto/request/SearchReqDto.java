package com.project.classroom.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchReqDto {
    private int limit;

    private int offset;

    private List<Map<String, Object>> filterBy;

    private List<Map<String, Object>> sortBy;
}
