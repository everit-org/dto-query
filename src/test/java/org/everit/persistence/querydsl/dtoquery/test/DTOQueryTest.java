package org.everit.persistence.querydsl.dtoquery.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.everit.persistence.querydsl.dtoquery.DTOQuery;
import org.everit.persistence.querydsl.dtoquery.PropertyQuery;
import org.everit.persistence.querydsl.dtoquery.test.beans.TableADTO;
import org.everit.persistence.querydsl.dtoquery.test.beans.TableBDTO;
import org.everit.persistence.querydsl.dtoquery.test.beans.TableCDTO;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableA;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableB;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableC;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.persistence.querydsl.support.ri.QuerydslSupportImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class DTOQueryTest {

  private static QuerydslSupport qdsl;

  @BeforeClass
  public static void beforeClass() {
    JdbcDataSource jdbcDataSource = new JdbcDataSource();
    jdbcDataSource.setUser("sa");
    jdbcDataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

    Configuration configuration = new Configuration(new H2Templates());
    DTOQueryTest.qdsl = new QuerydslSupportImpl(configuration, jdbcDataSource);

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
    DTOQueryTest.qdsl.execute((connection, configuration) -> {
      new SQLDeleteClause(connection, configuration, QTableC.tableC).execute();
      new SQLDeleteClause(connection, configuration, QTableB.tableB).execute();
      new SQLDeleteClause(connection, configuration, QTableA.tableA).execute();
    });
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

    DTOQueryTest.qdsl.execute((connection, configuration) -> {
      for (TableADTO tableA : testData) {
        new SQLInsertClause(connection, configuration, qTableA)
            .set(qTableA.tableAId, tableA.tableAId)
            .set(qTableA.tableAVc, tableA.tableAVc)
            .execute();

        for (TableBDTO tableB : tableA.tableBList) {
          new SQLInsertClause(connection, configuration, qTableB)
              .set(qTableB.tableBId, tableB.tableBId)
              .set(qTableB.tableBVc, tableB.tableBVc)
              .set(qTableB.tableAId, tableB.tableAId)
              .execute();

          for (TableCDTO tableC : tableB.tableCList) {
            new SQLInsertClause(connection, configuration, qTableC)
                .set(qTableC.tableCId, tableC.tableCId)
                .set(qTableC.tableCVc, tableC.tableCVc)
                .set(qTableC.tableBId, tableC.tableBId)
                .execute();
          }
        }
      }
    });
  }

  @Test
  public void t01_testQueryDTOLowLevel() {
    Collection<TableADTO> testData = createTestData();
    storeTestData(testData);

    DTOQueryTest.qdsl.execute((connection, configuration) -> {

      QTableA tableA = QTableA.tableA;
      QTableB tableB = QTableB.tableB;
      QTableC tableC = QTableC.tableC;

      DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
          .root(new SQLQuery<>()
              .select(Projections.fields(TableADTO.class, tableA.all()))
              .from(tableA)
              .orderBy(tableA.tableAId.asc()))

          .prop(new PropertyQuery<TableADTO, Long, TableBDTO>()
              .oneToManySetter((aDTO, bDTOList) -> aDTO.tableBList = bDTOList)
              .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
              .keyInPropertyDTOResolver((tableBDTO) -> tableBDTO.tableAId)
              .dtoQuery(DTOQuery
                  .create((Collection<Long> fks) -> new SQLQuery<>()
                      .select(Projections.fields(TableBDTO.class, tableB.all()))
                      .from(tableB)
                      .where(tableB.tableAId.in(fks))
                      .orderBy(tableB.tableBId.asc()))

                  .prop(new PropertyQuery<TableBDTO, Long, TableCDTO>()
                      .oneToManySetter((bDTO, cDTOList) -> bDTO.tableCList = cDTOList)
                      .keyInSourceDTOResolver(bDTO -> bDTO.tableBId)
                      .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                      .dtoQuery(DTOQuery.create(fks -> new SQLQuery<>()
                          .select(Projections.fields(TableCDTO.class, tableC.all()))
                          .from(tableC)
                          .where(tableC.tableBId.in(fks))
                          .orderBy(tableC.tableCId.asc()))))));

      Collection<TableADTO> tableACollection = dtoQuery.queryDTO(connection);

      Assert.assertEquals(testData.toString(), tableACollection.toString());

    });
  }

  @Test
  public void t02_testQueryDTORootFullTable() {
    Collection<TableADTO> testData = createTestData();
    storeTestData(testData);

    DTOQueryTest.qdsl.execute((connection, configuration) -> {

      QTableA tableA = QTableA.tableA;
      QTableB tableB = QTableB.tableB;
      QTableC tableC = QTableC.tableC;

      DTOQuery<Object, TableADTO> dtoQuery = DTOQuery
          .rootDTOFullTable(configuration, tableA, TableADTO.class)

          .prop(new PropertyQuery<TableADTO, Long, TableBDTO>()
              .oneToManySetter((aDTO, bDTOList) -> aDTO.tableBList = bDTOList)
              .keyInSourceDTOResolver((tableADTO) -> tableADTO.tableAId)
              .keyInPropertyDTOResolver((tableBDTO) -> tableBDTO.tableAId)
              .dtoQuery(
                  DTOQuery.dtoFullTable(configuration, tableB, TableBDTO.class, tableB.tableAId)
                      .prop(new PropertyQuery<TableBDTO, Long, TableCDTO>()
                          .oneToManySetter((bDTO, cDTOList) -> bDTO.tableCList = cDTOList)
                          .keyInSourceDTOResolver(bDTO -> bDTO.tableBId)
                          .keyInPropertyDTOResolver((cDTO) -> cDTO.tableBId)
                          .dtoQuery(DTOQuery.dtoFullTable(configuration, tableC, TableCDTO.class,
                              tableC.tableBId)))));

      Collection<TableADTO> tableACollection = dtoQuery.queryDTO(connection);

      Assert.assertEquals(testData.toString(), tableACollection.toString());

    });
  }

}
