package com.part2.findex.indexinfo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIndexInfo is a Querydsl query type for IndexInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIndexInfo extends EntityPathBase<IndexInfo> {

    private static final long serialVersionUID = -250924055L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIndexInfo indexInfo = new QIndexInfo("indexInfo");

    public final NumberPath<Double> baseIndex = createNumber("baseIndex", Double.class);

    public final StringPath basePointInTime = createString("basePointInTime");

    public final NumberPath<Double> employedItemsCount = createNumber("employedItemsCount", Double.class);

    public final BooleanPath favorite = createBoolean("favorite");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QIndexInfoBusinessKey indexInfoBusinessKey;

    public final EnumPath<SourceType> sourceType = createEnum("sourceType", SourceType.class);

    public QIndexInfo(String variable) {
        this(IndexInfo.class, forVariable(variable), INITS);
    }

    public QIndexInfo(Path<? extends IndexInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIndexInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIndexInfo(PathMetadata metadata, PathInits inits) {
        this(IndexInfo.class, metadata, inits);
    }

    public QIndexInfo(Class<? extends IndexInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.indexInfoBusinessKey = inits.isInitialized("indexInfoBusinessKey") ? new QIndexInfoBusinessKey(forProperty("indexInfoBusinessKey")) : null;
    }

}

