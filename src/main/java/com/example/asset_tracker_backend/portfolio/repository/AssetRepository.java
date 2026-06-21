package com.example.asset_tracker_backend.portfolio.repository;

import com.example.asset_tracker_backend.portfolio.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("""
        SELECT a.type, SUM(a.currentValue)
        FROM Asset a
        GROUP BY a.type
    """)
    List<Object[]> getAllocationByType();

    java.util.Optional<Asset> findByIdAndUserId(Long id, Integer userId);
}

