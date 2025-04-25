package com.part2.findex.indexdata.entity;

import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "index_data",
        uniqueConstraints = @UniqueConstraint(name = "uq_index_trade", columnNames = {"index_info_id", "base_date"}))
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IndexData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, foreignKey = @ForeignKey(name = "fk_index_info"))
    private IndexInfo indexInfo;

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Column(name = "source_type", nullable = false, length = 10)
    private String sourceType;

    @Column(name = "market_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal marketPrice;

    @Column(name = "closing_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal closingPrice;

    @Column(name = "high_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal highPrice;

    @Column(name = "low_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal lowPrice;

    @Column(name = "versus", nullable = false, precision = 18, scale = 2)
    private BigDecimal versus;

    @Column(name = "fluctuation_rate", nullable = false, precision = 8, scale = 3)
    private BigDecimal fluctuationRate;

    @Column(name = "trading_quantity", nullable = false)
    private Long tradingQuantity;

    @Column(name = "trading_price", nullable = false, precision = 24, scale = 0)
    private BigDecimal tradingPrice;

    @Column(name = "market_total_amount", nullable = false, precision = 24, scale = 0)
    private BigDecimal marketTotalAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(IndexDataUpdateRequest updateRequest) {
        this.marketPrice = updateRequest.marketPrice();
        this.closingPrice = updateRequest.closingPrice();
        this.highPrice = updateRequest.highPrice();
        this.lowPrice = updateRequest.lowPrice();
        this.versus = updateRequest.versus();
        this.fluctuationRate = updateRequest.fluctuationRate();
        this.tradingQuantity = updateRequest.tradingQuantity();
        this.tradingPrice = updateRequest.tradingPrice();
        this.marketTotalAmount = updateRequest.marketTotalAmount();
        this.createdAt = LocalDateTime.now();
    }
}


