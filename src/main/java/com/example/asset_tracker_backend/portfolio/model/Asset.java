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
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type; // CRYPTO, STOCK, GOLD

    private String symbol;

    @Column(name = "external_id")
    private String externalId;

    private Double quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "current_value")
    private Double currentValue;

    private String currency;

    @Column(name = "last_price_update")
    private LocalDateTime lastPriceUpdate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
