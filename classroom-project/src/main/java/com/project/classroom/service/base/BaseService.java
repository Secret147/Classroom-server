package com.project.classroom.service.base;

import com.project.classroom.dto.request.SearchReqDto;
import com.project.classroom.dto.response.SearchResDto;

import java.util.List;

public interface BaseService<T> {
    void save(T object);
    T findById(Long id);
    List<T> findAll();
    void deleteById(Long id);

    SearchResDto search(SearchReqDto request);

    boolean existsById(Long id);

    long getTotal();
}
