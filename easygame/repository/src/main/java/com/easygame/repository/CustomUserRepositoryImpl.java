package com.easygame.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomUserRepositoryImpl implements CustomUserRepository {
    @Autowired
    private EntityManager entityManager;
}
