package org.everit.persistence.querydsl.dtoquery;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.querydsl.sql.SQLQuery;

/**
 * Queries a DTO class.
 */
public class DTOQuery<RK, T> {

  public static <RK, T> DTOQuery<RK, T> create(
      Function<Collection<RK>, SQLQuery<T>> queryGenerator) {

    return new DTOQuery<>(queryGenerator);
  }

  protected List<PropertyQuery<T, ?, ?>> propertyQueries = new ArrayList<>();

  protected Function<Collection<RK>, SQLQuery<T>> queryGenerator;

  protected DTOQuery(Function<Collection<RK>, SQLQuery<T>> queryGenerator) {
    this.queryGenerator = queryGenerator;
  }

  public <P, K> DTOQuery<RK, T> prop(PropertyQuery<T, ?, ?> propertyQuery) {

    this.propertyQueries.add(propertyQuery);
    return this;
  }

  public Collection<T> queryDTO(Connection connection) {
    SQLQuery<T> query = this.queryGenerator.apply(Collections.emptyList());
    List<T> resultSet = query.clone(connection).fetch();

    queryDTOProperties(resultSet);

    return resultSet;
  }

  protected void queryDTOProperties(Collection<T> resultSet) {

    for (PropertyQuery<T, ?, ?> propertyQuery : this.propertyQueries) {
      queryDTOProperty(resultSet, propertyQuery);
    }
  }

  protected <FK, P> void queryDTOProperty(Collection<T> resultSet,
      PropertyQuery<T, FK, P> propertyQuery) {

  }

}
