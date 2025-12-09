package com.example.jwtapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.jwtapp.entity.JwtTokenEntity;

public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, Long> {
}
