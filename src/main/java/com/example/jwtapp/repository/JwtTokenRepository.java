package com.example.jwtapp.repository;

import com.example.jwtapp.domain.entity.JwtTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, Long> {
}
