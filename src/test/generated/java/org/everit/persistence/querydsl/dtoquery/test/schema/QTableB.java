/*
 * Copyright © 2011 Everit Kft. (http://www.everit.biz)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.persistence.querydsl.dtoquery.test.schema;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QTableB is a Querydsl query type for QTableB
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QTableB extends com.querydsl.sql.RelationalPathBase<QTableB> {

    private static final long serialVersionUID = -1915059330;

    public static final QTableB tableB = new QTableB("TABLE_B");

    public final NumberPath<Long> tableAId = createNumber("tableAId", Long.class);

    public final NumberPath<Long> tableBId = createNumber("tableBId", Long.class);

    public final StringPath tableBVc = createString("tableBVc");

    public final com.querydsl.sql.PrimaryKey<QTableB> tableBPk = createPrimaryKey(tableBId);

    public final com.querydsl.sql.ForeignKey<QTableA> tableBAFk = createForeignKey(tableAId, "TABLE_A_ID");

    public final com.querydsl.sql.ForeignKey<QTableC> _tableCBFk = createInvForeignKey(tableBId, "TABLE_B_ID");

    public QTableB(String variable) {
        super(QTableB.class, forVariable(variable), "PUBLIC", "TABLE_B");
        addMetadata();
    }

    public QTableB(String variable, String schema, String table) {
        super(QTableB.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTableB(String variable, String schema) {
        super(QTableB.class, forVariable(variable), schema, "TABLE_B");
        addMetadata();
    }

    public QTableB(Path<? extends QTableB> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "TABLE_B");
        addMetadata();
    }

    public QTableB(PathMetadata metadata) {
        super(QTableB.class, metadata, "PUBLIC", "TABLE_B");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(tableAId, ColumnMetadata.named("TABLE_A_ID").withIndex(3).ofType(Types.BIGINT).withSize(19));
        addMetadata(tableBId, ColumnMetadata.named("TABLE_B_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(tableBVc, ColumnMetadata.named("TABLE_B_VC").withIndex(2).ofType(Types.VARCHAR).withSize(2000));
    }

}

