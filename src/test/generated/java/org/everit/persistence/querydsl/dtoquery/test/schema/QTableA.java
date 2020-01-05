package org.everit.persistence.querydsl.dtoquery.test.schema;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QTableA is a Querydsl query type for QTableA
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QTableA extends com.querydsl.sql.RelationalPathBase<QTableA> {

    private static final long serialVersionUID = -1915059331;

    public static final QTableA tableA = new QTableA("TABLE_A");

    public final NumberPath<Long> tableAId = createNumber("tableAId", Long.class);

    public final StringPath tableAVc = createString("tableAVc");

    public final com.querydsl.sql.PrimaryKey<QTableA> tableAPk = createPrimaryKey(tableAId);

    public final com.querydsl.sql.ForeignKey<QTableB> _tableBAFk = createInvForeignKey(tableAId, "TABLE_A_ID");

    public QTableA(String variable) {
        super(QTableA.class, forVariable(variable), "PUBLIC", "TABLE_A");
        addMetadata();
    }

    public QTableA(String variable, String schema, String table) {
        super(QTableA.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTableA(String variable, String schema) {
        super(QTableA.class, forVariable(variable), schema, "TABLE_A");
        addMetadata();
    }

    public QTableA(Path<? extends QTableA> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "TABLE_A");
        addMetadata();
    }

    public QTableA(PathMetadata metadata) {
        super(QTableA.class, metadata, "PUBLIC", "TABLE_A");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(tableAId, ColumnMetadata.named("TABLE_A_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(tableAVc, ColumnMetadata.named("TABLE_A_VC").withIndex(2).ofType(Types.VARCHAR).withSize(2000));
    }

}

