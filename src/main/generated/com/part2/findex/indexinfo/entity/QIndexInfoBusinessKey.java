package com.part2.findex.indexinfo.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QIndexInfoBusinessKey is a Querydsl query type for IndexInfoBusinessKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QIndexInfoBusinessKey extends BeanPath<IndexInfoBusinessKey> {

    private static final long serialVersionUID = -1691916586L;

    public static final QIndexInfoBusinessKey indexInfoBusinessKey = new QIndexInfoBusinessKey("indexInfoBusinessKey");

    public final StringPath indexClassification = createString("indexClassification");

    public final StringPath indexName = createString("indexName");

    public QIndexInfoBusinessKey(String variable) {
        super(IndexInfoBusinessKey.class, forVariable(variable));
    }

    public QIndexInfoBusinessKey(Path<? extends IndexInfoBusinessKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIndexInfoBusinessKey(PathMetadata metadata) {
        super(IndexInfoBusinessKey.class, metadata);
    }

}

