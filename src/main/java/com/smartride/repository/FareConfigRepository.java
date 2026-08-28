package com.smartride.repository;


import com.smartride.model.FareConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FareConfigRepository extends JpaRepository<FareConfig, Long> {
    Optional<FareConfig> findByActiveTrue();
}