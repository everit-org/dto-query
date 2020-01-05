package org.everit.persistence.querydsl.dtoquery.test;

import org.everit.persistence.querydsl.dtoquery.test.schema.QTableA;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableB;
import org.everit.persistence.querydsl.dtoquery.test.schema.QTableC;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.persistence.querydsl.support.ri.QuerydslSupportImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.H2Templates;
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

  private void createTestData() {
    DTOQueryTest.qdsl.execute((connection, configuration) -> {
      new SQLInsertClause(connection, configuration, QTableA.tableA)
          .set(QTableA.tableA.tableAId, 0L)
          .set(QTableA.tableA.tableAVc, "table a value 0")
          .execute();

      new SQLInsertClause(connection, configuration, QTableB.tableB)
          .set(QTableB.tableB.tableBId, 0L)
          .set(QTableB.tableB.tableBVc, "table b value 0")
          .set(QTableB.tableB.tableAId, 0L)
          .execute();

      new SQLInsertClause(connection, configuration, QTableC.tableC)
          .set(QTableC.tableC.tableCId, 0L)
          .set(QTableC.tableC.tableCVc, "table a value 0")
          .set(QTableC.tableC.tableBId, 0L)
          .execute();
    });
  }

  @Test
  public void t01_testQueryDTO() {
    createTestData();
  }
}
