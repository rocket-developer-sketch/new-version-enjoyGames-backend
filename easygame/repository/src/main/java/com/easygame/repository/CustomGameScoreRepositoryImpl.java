package com.easygame.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CustomGameScoreRepositoryImpl implements CustomGameScoreRepository{
    @Autowired
    private EntityManager entityManager;
}
