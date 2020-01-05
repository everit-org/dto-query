package org.everit.persistence.querydsl.dtoquery.test.schema;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QTableC is a Querydsl query type for QTableC
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QTableC extends com.querydsl.sql.RelationalPathBase<QTableC> {

    private static final long serialVersionUID = -1915059329;

    public static final QTableC tableC = new QTableC("TABLE_C");

    public final NumberPath<Long> tableBId = createNumber("tableBId", Long.class);

    public final NumberPath<Long> tableCId = createNumber("tableCId", Long.class);

    public final StringPath tableCVc = createString("tableCVc");

    public final com.querydsl.sql.PrimaryKey<QTableC> tableCPk = createPrimaryKey(tableCId);

    public final com.querydsl.sql.ForeignKey<QTableB> tableCBFk = createForeignKey(tableBId, "TABLE_B_ID");

    public QTableC(String variable) {
        super(QTableC.class, forVariable(variable), "PUBLIC", "TABLE_C");
        addMetadata();
    }

    public QTableC(String variable, String schema, String table) {
        super(QTableC.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTableC(String variable, String schema) {
        super(QTableC.class, forVariable(variable), schema, "TABLE_C");
        addMetadata();
    }

    public QTableC(Path<? extends QTableC> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "TABLE_C");
        addMetadata();
    }

    public QTableC(PathMetadata metadata) {
        super(QTableC.class, metadata, "PUBLIC", "TABLE_C");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(tableBId, ColumnMetadata.named("TABLE_B_ID").withIndex(3).ofType(Types.BIGINT).withSize(19));
        addMetadata(tableCId, ColumnMetadata.named("TABLE_C_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(tableCVc, ColumnMetadata.named("TABLE_C_VC").withIndex(2).ofType(Types.VARCHAR).withSize(2000));
    }

}

