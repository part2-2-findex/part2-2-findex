package com.part2.findex.autosync.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAutoSyncConfig is a Querydsl query type for AutoSyncConfig
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAutoSyncConfig extends EntityPathBase<AutoSyncConfig> {

    private static final long serialVersionUID = -324554439L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAutoSyncConfig autoSyncConfig = new QAutoSyncConfig("autoSyncConfig");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.part2.findex.indexinfo.entity.QIndexInfo indexInfo;

    public QAutoSyncConfig(String variable) {
        this(AutoSyncConfig.class, forVariable(variable), INITS);
    }

    public QAutoSyncConfig(Path<? extends AutoSyncConfig> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAutoSyncConfig(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAutoSyncConfig(PathMetadata metadata, PathInits inits) {
        this(AutoSyncConfig.class, metadata, inits);
    }

    public QAutoSyncConfig(Class<? extends AutoSyncConfig> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.indexInfo = inits.isInitialized("indexInfo") ? new com.part2.findex.indexinfo.entity.QIndexInfo(forProperty("indexInfo"), inits.get("indexInfo")) : null;
    }

}

