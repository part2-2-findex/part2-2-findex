package com.part2.findex.autosync.entity;

import com.part2.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auto_sync_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AutoSyncConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean enabled;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    public void changeEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
