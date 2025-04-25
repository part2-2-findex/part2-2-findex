package com.part2.findex.indexdata.entity;

import com.part2.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "index_data",
        uniqueConstraints = @UniqueConstraint(name = "uq_index_trade", columnNames = {"index_info_id", "trade_date"}))
@Getter
@Setter
public class IndexData {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, foreignKey = @ForeignKey(name = "fk_index_info"))
    private IndexInfo indexInfo;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "source_type", nullable = false, length = 10)
    private String sourceType;

    @Column(name = "open_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal openPrice;

    @Column(name = "close_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal closePrice;

    @Column(name = "high_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal highPrice;

    @Column(name = "low_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal lowPrice;

    @Column(name = "diff", nullable = false, precision = 18, scale = 2)
    private BigDecimal diff;

    @Column(name = "change_rate", nullable = false, precision = 8, scale = 3)
    private BigDecimal changeRate;

    @Column(name = "volume", nullable = false)
    private Long volume;

    @Column(name = "trading_value", nullable = false, precision = 24, scale = 0)
    private BigDecimal tradingValue;

    @Column(name = "market_cap", nullable = false, precision = 24, scale = 0)
    private BigDecimal marketCap;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}