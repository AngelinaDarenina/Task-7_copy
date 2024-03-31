package ru.itmentor.spring.boot_security.demo.service;

import ru.itmentor.spring.boot_security.demo.model.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findById(long id);
}
