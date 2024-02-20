package com.example.articlesapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.articlesapi.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    
    Optional<User> findByUsername(String username);

    Integer findIdByUsername(String username);
}
