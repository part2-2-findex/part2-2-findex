package com.part2.findex.indexdata.entity;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.SourceType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "index_data",
        uniqueConstraints = @UniqueConstraint(name = "uq_index_trade", columnNames = {"index_info_id", "trade_date"}))
@Getter
@EntityListeners(AuditingEntityListener.class)
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
    @Enumerated(value = EnumType.STRING)
    private SourceType sourceType;

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

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected IndexData() {
    }

    public IndexData(IndexInfo indexInfo, LocalDate baseDate, SourceType sourceType, BigDecimal marketPrice, BigDecimal closingPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal versus, BigDecimal fluctuationRate, Long tradingQuantity, BigDecimal tradingPrice, BigDecimal marketTotalAmount) {
        this.indexInfo = indexInfo;
        this.baseDate = baseDate;
        this.sourceType = sourceType;
        this.marketPrice = marketPrice;
        this.closingPrice = closingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.versus = versus;
        this.fluctuationRate = fluctuationRate;
        this.tradingQuantity = tradingQuantity;
        this.tradingPrice = tradingPrice;
        this.marketTotalAmount = marketTotalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexData indexData = (IndexData) o;
        if (!Objects.equals(indexInfo, indexData.indexInfo)) return false;
        return Objects.equals(baseDate, indexData.baseDate);
    }

    @Override
    public int hashCode() {
        int result = indexInfo != null ? indexInfo.hashCode() : 0;
        result = 31 * result + (baseDate != null ? baseDate.hashCode() : 0);

        return result;
    }
}