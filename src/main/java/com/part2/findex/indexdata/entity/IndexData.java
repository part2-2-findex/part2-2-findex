package com.part2.findex.indexdata.entity;

import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
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
import java.util.function.Consumer;

@Entity
@Table(name = "index_data",
        uniqueConstraints = @UniqueConstraint(name = "uq_index_trade", columnNames = {"index_info_id", "base_date"}))
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

    public void update(IndexDataUpdateRequest updateRequest) {
        updateIfValidBigDecimal(updateRequest.marketPrice(), val -> this.marketPrice = val);
        updateIfValidBigDecimal(updateRequest.closingPrice(), val -> this.closingPrice = val);
        updateIfValidBigDecimal(updateRequest.highPrice(), val -> this.highPrice = val);
        updateIfValidBigDecimal(updateRequest.lowPrice(), val -> this.lowPrice = val);
        updateIfValidBigDecimal(updateRequest.versus(), val -> this.versus = val);
        updateIfValidBigDecimal(updateRequest.fluctuationRate(), val -> this.fluctuationRate = val);
        updateIfValidBigDecimal(updateRequest.tradingPrice(), val -> this.tradingPrice = val);
        updateIfValidBigDecimal(updateRequest.marketTotalAmount(), val -> this.marketTotalAmount = val);

        // Long 타입 필드
        updateIfValidLong(updateRequest.tradingQuantity(), val -> this.tradingQuantity = val);
    }

    private void updateIfValidBigDecimal(BigDecimal newValue, Consumer<BigDecimal> setter) {
        if (newValue != null && newValue.compareTo(BigDecimal.ZERO) != 0) {
            setter.accept(newValue);
        }
    }

    private void updateIfValidLong(Long newValue, Consumer<Long> setter) {
        if (newValue != null && newValue != 0L) {
            setter.accept(newValue);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexData)) return false;

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