package com.project.classroom.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class BaseServerImpl<T, R extends JpaRepository<T, Long>> implements BaseService<T>{
    @Autowired
    private R baseRepository;

    @Override
    public void save(T object) {
        baseRepository.save(object);
    }

    @Override
    public T findById(Long id) {
        return baseRepository.findById(id).orElseThrow();
    }

    @Override
    public List<T> findAll() {
        return baseRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        baseRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return baseRepository.existsById(id);
    }

    @Override
    public long getTotal() {
        return baseRepository.count();
    }


}
