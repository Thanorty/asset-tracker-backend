package com.example.asset_tracker_backend.portfolio.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type; // CRYPTO, STOCK, GOLD

    private Double quantity;

    @Column(name = "current_value")
    private Double currentValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
