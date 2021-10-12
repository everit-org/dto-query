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
package org.everit.persistence.querydsl.dtoquery.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.everit.persistence.querydsl.dtoquery.DTOQuery;
import org.everit.persistence.querydsl.dtoquery.DTOWithKeys;
import org.everit.persistence.querydsl.dtoquery.PropertyQuery;
import org.everit.persistence.querydsl.dtoquery.QDTOWithKeys;
import org.everit.persistence.querydsl.dtoquery.test.beans.ManyToOneTableADTO;
import org.everit.persistence.querydsl.dtoquery.test.beans.ManyToOneTableBDTO;
import org.everit.persistence.querydsl.dtoquery.test.beans.TableADTO;
import org.everit.persistence.querydsl.dtoquery.test.beans.TableAWithStringCollectionDTO;
import org.everit.persistence.querydsl.dtoquery.test.beans.TableBDTO;
import org.everit.persistence.querydsl.dtoquery.test.beans.TableCDTO;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableA;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableB;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableC;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class DTOQueryTest {

  private static SQLQueryFactory qdsl;

  @BeforeClass
  public static void beforeClass() {
    JdbcDataSource jdbcDataSource = new JdbcDataSource();
    jdbcDataSource.setUser("sa");
    jdbcDataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

    Configuration configuration = new Configuration(new H2Templates());
    DTOQueryTest.qdsl = new SQLQueryFactory(configuration, jdbcDataSource);

    try (AutoCloseableWrapper<DatabaseConnection> dbConnectionWrapper =
        new AutoCloseableWrapper<>(
            new JdbcConnection(jdbcDataSource.getConnection()), j -> j.close())) {

      Liquibase liquibase = new Liquibase("META-INF/db-changelog.xml",
          new ClassLoaderResourceAccessor(DTOQueryTest.class.getClassLoader()),
          dbConnectionWrapper.getWrapped());

      liquibase.update((String) null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @After
  public void afterTest() {
    DTOQueryTest.qdsl.delete(QTableC.tableC).execute();
    DTOQueryTest.qdsl.delete(QTableB.tableB).execute();
    DTOQueryTest.qdsl.delete(QTableA.tableA).execute();
  }

  private Collection<TableADTO> createTestData() {
    List<TableADTO> tableAList = new ArrayList<>();

    TableADTO a0 = new TableADTO();
    tableAList.add(a0);
    a0.tableAId = 0;
    a0.tableAVc = "a:0";
    a0.tableBList = new ArrayList<>();

    // a:0, b:0
    TableBDTO b0 = new TableBDTO();
    a0.tableBList.add(b0);
    b0.tableAId = 0L;
    b0.tableBVc = "a:0, b:0";
    b0.tableCList = new ArrayList<>();

    TableCDTO c0 = new TableCDTO();
    b0.tableCList.add(c0);
    c0.tableCId = 0;
    c0.tableCVc = "a:0, b:0, c: 0";
    c0.tableBId = 0L;

    TableCDTO c1 = new TableCDTO();
    b0.tableCList.add(c1);
    c1.tableCId = 1;
    c1.tableCVc = "a:0, b:0, c: 1";
    c1.tableBId = 0L;

    // a:0, b:1
    TableBDTO b1 = new TableBDTO();
    a0.tableBList.add(b1);
    b1.tableBId = 1;
    b1.tableAId = 0L;
    b1.tableBVc = "a:0, b:1";
    b1.tableCList = new ArrayList<>();

    TableCDTO c2 = new TableCDTO();
    b1.tableCList.add(c2);
    c2.tableCId = 2;
    c2.tableCVc = "a:0, b:1, c: 2";
    c2.tableBId = 1L;

    // a:0, b:2
    TableBDTO b2 = new TableBDTO();
    a0.tableBList.add(b2);
    b2.tableBId = 2;
    b2.tableAId = 0L;
    b2.tableBVc = "a:0, b:2";

    TableADTO a1 = new TableADTO();
    tableAList.add(a1);
    a1.tableAId = 1;
    a1.tableAVc = "a:1";
    a1.tableBList = new ArrayList<>();

    return tableAList;
  }

  private void storeTestData(Collection<TableADTO> testData) {
    QTableA qTableA = QTableA.tableA;
    QTableB qTableB = QTableB.tableB;
    QTableC qTableC = QTableC.tableC;

    for (TableADTO tableA : testData) {
      DTOQueryTest.qdsl.insert(qTableA)
          .set(qTableA.tableAId, tableA.tableAId)
          .set(qTableA.tableAVc, tableA.tableAVc)
          .execute();

      for (TableBDTO tableB : tableA.tableBList) {
        DTOQueryTest.qdsl.insert(qTableB)
            .set(qTableB.tableBId, tableB.tableBId)
            .set(qTableB.tableBVc, tableB.tableBVc)
            .set(qTableB.tableAId, tableB.tableAId)
            .execute();

        for (TableCDTO tableC : tableB.tableCList) {
          DTOQueryTest.qdsl.insert(qTableC)
              .set(qTableC.tableCId, tableC.tableCId)
              .set(qTableC.tableCVc, tableC.tableCVc)
              .set(qTableC.tableBId, tableC.tableBId)
              .execute();
        }
      }
    }
  }

  @Test
  public void t01_testQueryDTOLowLevel() {
    Collection<TableADTO> testData = createTestData();
    storeTestData(testData);

    QTableA tableA = QTableA.tableA;
    QTableB tableB = QTableB.tableB;
    QTableC tableC = QTableC.tableC;

    DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
        .root(DTOQueryTest.qdsl
            .select(Projections.fields(TableADTO.class, tableA.all()))
            .from(tableA)
            .orderBy(tableA.tableAId.asc()))

        .prop(new PropertyQuery<TableADTO, Long, TableBDTO>()
            .oneToManySetter((aDTO, bDTOList) -> aDTO.tableBList = bDTOList)
            .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
            .keyInPropertyDTOResolver((tableBDTO) -> tableBDTO.tableAId)
            .dtoQuery(DTOQuery
                .create((Collection<Long> fks) -> DTOQueryTest.qdsl
                    .select(Projections.fields(TableBDTO.class, tableB.all()))
                    .from(tableB)
                    .where(tableB.tableAId.in(fks))
                    .orderBy(tableB.tableBId.asc()))

                .prop(new PropertyQuery<TableBDTO, Long, TableCDTO>()
                    .oneToManySetter((bDTO, cDTOList) -> bDTO.tableCList = cDTOList)
                    .keyInSourceDTOResolver(bDTO -> bDTO.tableBId)
                    .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                    .dtoQuery(DTOQuery.create(fks -> DTOQueryTest.qdsl
                        .select(Projections.fields(TableCDTO.class, tableC.all()))
                        .from(tableC)
                        .where(tableC.tableBId.in(fks))
                        .orderBy(tableC.tableCId.asc()))))));

    Collection<TableADTO> tableACollection = dtoQuery.queryDTO();

    Assert.assertEquals(testData.toString(), tableACollection.toString());
  }

  @Test
  public void t02_testQueryDTORootFullTable() {
    Collection<TableADTO> testData = createTestData();
    storeTestData(testData);

    QTableA tableA = QTableA.tableA;
    QTableB tableB = QTableB.tableB;
    QTableC tableC = QTableC.tableC;

    DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
        .rootDTOFullTable(DTOQueryTest.qdsl, tableA, TableADTO.class)

        .prop(new PropertyQuery<TableADTO, Long, TableBDTO>()
            .oneToManySetter((aDTO, bDTOList) -> aDTO.tableBList = bDTOList)
            .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
            .keyInPropertyDTOResolver((tableBDTO) -> tableBDTO.tableAId)
            .dtoQuery(
                DTOQuery.dtoFullTable(DTOQueryTest.qdsl, tableB, TableBDTO.class, tableB.tableAId)
                    .prop(new PropertyQuery<TableBDTO, Long, TableCDTO>()
                        .oneToManySetter((bDTO, cDTOList) -> bDTO.tableCList = cDTOList)
                        .keyInSourceDTOResolver(bDTO -> bDTO.tableBId)
                        .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                        .dtoQuery(DTOQuery.dtoFullTable(DTOQueryTest.qdsl, tableC, TableCDTO.class,
                            tableC.tableBId)))));

    Collection<TableADTO> tableACollection = dtoQuery.queryDTO();

    Assert.assertEquals(testData.toString(), tableACollection.toString());

  }

  @Test
  public void t03_testQDTOWitKeysWithSimpleExpression() {
    Collection<TableADTO> testData = createTestData();
    storeTestData(testData);

    QTableA qTableA = QTableA.tableA;
    QTableB qTableB = QTableB.tableB;

    DTOQuery<Object, TableAWithStringCollectionDTO> dtoQuery = DTOQuery
        .root(DTOQueryTest.qdsl
            .select(Projections.fields(TableAWithStringCollectionDTO.class, qTableA.all()))
            .from(qTableA)
            .orderBy(qTableA.tableAId.asc()))

        .prop(new PropertyQuery<TableAWithStringCollectionDTO, Long, DTOWithKeys<String>>()
            .oneToManySetter((aDTO, dtoWithKeys) -> aDTO.tableBVcList =
                dtoWithKeys.stream().map(e -> e.dto).collect(Collectors.toList()))
            .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
            .keyInPropertyDTOResolver((dtoWithKeys) -> dtoWithKeys.keys.get(qTableB.tableAId))
            .dtoQuery(DTOQuery
                .create((Collection<Long> fks) -> DTOQueryTest.qdsl
                    .select(new QDTOWithKeys<>(qTableB.tableBVc, qTableB.tableAId))
                    .from(qTableB)
                    .where(qTableB.tableAId.in(fks))
                    .orderBy(qTableB.tableBId.asc()))

            ));

    Collection<TableAWithStringCollectionDTO> actual = dtoQuery.queryDTO();

    Collection<TableAWithStringCollectionDTO> expected = new ArrayList<>();
    for (TableADTO tableA : testData) {
      TableAWithStringCollectionDTO tableAWithStringCollection =
          new TableAWithStringCollectionDTO();
      tableAWithStringCollection.tableAId = tableA.tableAId;
      tableAWithStringCollection.tableAVc = tableA.tableAVc;

      tableAWithStringCollection.tableBVcList =
          tableA.tableBList.stream().map(e -> e.tableBVc).collect(Collectors.toList());

      expected.add(tableAWithStringCollection);
    }

    Assert.assertEquals(expected.toString(), actual.toString());

  }

  @Test
  public void t04_testDTOWithKeysWithBean() {
    Collection<TableADTO> testData = createTestData();
    storeTestData(testData);

    QTableA tableA = QTableA.tableA;
    QTableB tableB = QTableB.tableB;
    QTableC tableC = QTableC.tableC;

    DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
        .rootDTOFullTable(DTOQueryTest.qdsl, tableA, TableADTO.class)

        .prop(new PropertyQuery<TableADTO, Long, DTOWithKeys<TableBDTO>>()
            .oneToManySetter((aDTO, b) -> aDTO.tableBList =
                b.stream().map(e -> e.dto).collect(Collectors.toList()))
            .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
            .keyInPropertyDTOResolver((b) -> b.keys.get(tableB.tableAId))
            .dtoQuery(
                DTOQuery
                    .create((Collection<Long> tableAIds) -> DTOQueryTest.qdsl
                        .select(
                            new QDTOWithKeys<>(
                                Projections.fields(TableBDTO.class, tableB.all()),
                                tableB.tableAId))
                        .from(tableB).where(tableB.tableAId.in(tableAIds)))
                    .prop(new PropertyQuery<DTOWithKeys<TableBDTO>, Long, TableCDTO>()
                        .oneToManySetter((bDTO, cDTOList) -> bDTO.dto.tableCList = cDTOList)
                        .keyInSourceDTOResolver(bDTO -> bDTO.dto.tableBId)
                        .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                        .dtoQuery(DTOQuery.dtoFullTable(DTOQueryTest.qdsl, tableC, TableCDTO.class,
                            tableC.tableBId)))));

    Collection<TableADTO> tableACollection = dtoQuery.queryDTO();

    Assert.assertEquals(testData.toString(), tableACollection.toString());
  }

  @Test
  public void t05_testQueryDTOLowLevelWithConnectionInQuery() {
    Collection<TableADTO> testData = createTestData();
    storeTestData(testData);

    QTableA tableA = QTableA.tableA;
    QTableB tableB = QTableB.tableB;
    QTableC tableC = QTableC.tableC;

    DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
        .root(DTOQueryTest.qdsl
            .select(Projections.fields(TableADTO.class, tableA.all()))
            .from(tableA)
            .orderBy(tableA.tableAId.asc()))

        .prop(new PropertyQuery<TableADTO, Long, TableBDTO>()
            .oneToManySetter((aDTO, bDTOList) -> aDTO.tableBList = bDTOList)
            .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
            .keyInPropertyDTOResolver((tableBDTO) -> tableBDTO.tableAId)
            .dtoQuery(DTOQuery
                .create((Collection<Long> fks) -> DTOQueryTest.qdsl
                    .select(Projections.fields(TableBDTO.class, tableB.all()))
                    .from(tableB)
                    .where(tableB.tableAId.in(fks))
                    .orderBy(tableB.tableBId.asc()))

                .prop(new PropertyQuery<TableBDTO, Long, TableCDTO>()
                    .oneToManySetter((bDTO, cDTOList) -> bDTO.tableCList = cDTOList)
                    .keyInSourceDTOResolver(bDTO -> bDTO.tableBId)
                    .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                    .dtoQuery(DTOQuery.create(fks -> DTOQueryTest.qdsl
                        .select(Projections.fields(TableCDTO.class, tableC.all()))
                        .from(tableC)
                        .where(tableC.tableBId.in(fks))
                        .orderBy(tableC.tableCId.asc()))))));

    Collection<TableADTO> tableACollection = dtoQuery.queryDTO();

    Assert.assertEquals(testData.toString(), tableACollection.toString());
  }

  @Test
  public void t06_testManyToOne() {
    QTableA qTableA = QTableA.tableA;
    QTableB qTableB = QTableB.tableB;

    DTOQueryTest.qdsl.insert(qTableA).set(qTableA.tableAId, 0l).set(qTableA.tableAVc, "test")
        .execute();

    DTOQueryTest.qdsl.insert(qTableB).set(qTableB.tableBId, 0l).set(qTableB.tableBVc, "test")
        .set(qTableB.tableAId, 0l).execute();

    DTOQueryTest.qdsl.insert(qTableB).set(qTableB.tableBId, 1l).set(qTableB.tableBVc, "test")
        .set(qTableB.tableAId, 0l).execute();

    List<ManyToOneTableBDTO> tableBRecords = DTOQuery.root(DTOQueryTest.qdsl
        .select(new QDTOWithKeys<>(
            Projections.fields(ManyToOneTableBDTO.class, qTableB.tableBId, qTableB.tableBVc),
            qTableB.tableAId))
        .from(qTableB))
        .prop(new PropertyQuery<DTOWithKeys<ManyToOneTableBDTO>, Long, ManyToOneTableADTO>()
            .keyInSourceDTOResolver(s -> s.keys.get(qTableB.tableAId))
            .keyInPropertyDTOResolver(p -> p.tableAId)
            .setter((s, p) -> s.dto.tableA = p)
            .dtoQuery(DTOQuery.create(tableAIds -> DTOQueryTest.qdsl.select(Projections
                .fields(ManyToOneTableADTO.class, qTableA.tableAId, qTableA.tableAVc))
                .from(qTableA)
                .where(qTableA.tableAId.in(tableAIds)))))
        .queryDTO().stream().map(r -> r.dto).collect(Collectors.toList());

    Assert.assertTrue(tableBRecords.stream().allMatch(b -> b.tableA.tableAId == 0));
  }

  @Test
  public void t07_test_ManyToOneWithOneNullFK() {
    QTableB qTableB = QTableB.tableB;

    DTOQueryTest.qdsl.insert(qTableB).set(qTableB.tableBId, 0l).set(qTableB.tableBVc, "test")
        .execute();

    DTOQuery.root(DTOQueryTest.qdsl
        .select(new QDTOWithKeys<>(
            Projections.fields(ManyToOneTableBDTO.class, qTableB.tableBId, qTableB.tableBVc),
            qTableB.tableAId))
        .from(qTableB))
        .prop(new PropertyQuery<DTOWithKeys<ManyToOneTableBDTO>, Long, ManyToOneTableADTO>()
            .keyInSourceDTOResolver(s -> s.keys.get(qTableB.tableAId))
            .keyInPropertyDTOResolver(p -> p.tableAId)
            .setter((s, p) -> s.dto.tableA = p)
            .dtoQuery(DTOQuery.create(tableAIds -> {
              Assert
                  .fail("In case of only null fks, dtoquery of property should not even be called");
              return null;
            })))
        .queryDTO().stream().map(r -> r.dto).collect(Collectors.toList());
  }

  @Test
  public void t08_test_ManyToOneWithNullFK() {
    QTableA qTableA = QTableA.tableA;
    QTableB qTableB = QTableB.tableB;

    DTOQueryTest.qdsl.insert(qTableA).set(qTableA.tableAId, 0l).set(qTableA.tableAVc, "test")
        .execute();

    DTOQueryTest.qdsl.insert(qTableB).set(qTableB.tableBId, 0l).set(qTableB.tableBVc, "test")
        .execute();

    DTOQueryTest.qdsl.insert(qTableB).set(qTableB.tableBId, 1l).set(qTableB.tableBVc, "test")
        .set(qTableB.tableAId, 0l).execute();

    List<ManyToOneTableBDTO> tableBRecords = DTOQuery.root(DTOQueryTest.qdsl
        .select(new QDTOWithKeys<>(
            Projections.fields(ManyToOneTableBDTO.class, qTableB.tableBId, qTableB.tableBVc),
            qTableB.tableAId))
        .from(qTableB))
        .prop(new PropertyQuery<DTOWithKeys<ManyToOneTableBDTO>, Long, ManyToOneTableADTO>()
            .keyInSourceDTOResolver(s -> s.keys.get(qTableB.tableAId))
            .keyInPropertyDTOResolver(p -> p.tableAId)
            .setter((s, p) -> s.dto.tableA = p)
            .dtoQuery(DTOQuery.create(tableAIds -> DTOQueryTest.qdsl.select(Projections
                .fields(ManyToOneTableADTO.class, qTableA.tableAId, qTableA.tableAVc))
                .from(qTableA)
                .where(qTableA.tableAId.in(tableAIds)))))
        .queryDTO().stream().map(r -> r.dto).collect(Collectors.toList());

    Assert.assertEquals(1, tableBRecords.stream().filter(r -> r.tableA == null).count());

    Assert.assertEquals(1,
        tableBRecords.stream().filter(r -> r.tableA != null && r.tableA.tableAId == 0).count());
  }
}
