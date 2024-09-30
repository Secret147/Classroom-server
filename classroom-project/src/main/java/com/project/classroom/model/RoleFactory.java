package com.project.classroom.model;

import com.project.classroom.entity.Role;
import com.project.classroom.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.relation.RoleNotFoundException;

@Component
public class RoleFactory {
    @Autowired
    RoleRepository roleRepository;

    public Role getInstance(String role) throws RoleNotFoundException {
        switch (role) {
            case "admin" -> {
                return roleRepository.findByName(RoleEnum.ROLE_ADMIN);
            }
            case "user" -> {
                return roleRepository.findByName(RoleEnum.ROLE_USER);
            }
            case "super_admin" -> {
                return roleRepository.findByName(RoleEnum.ROLE_SUPER_ADMIN);
            }
            default -> throw new RoleNotFoundException("Role not found for " +  role);
        }
    }
}

