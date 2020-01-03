package org.everit.persistence.querydsl.dtoquery;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

  protected Map<String, DTOQuery<?, ?>> propertyQueries = new HashMap<>();

  protected Function<Collection<RK>, SQLQuery<T>> queryGenerator;

  protected DTOQuery(Function<Collection<RK>, SQLQuery<T>> queryGenerator) {
    this.queryGenerator = queryGenerator;
  }

  public <P, K> DTOQuery<RK, T> propertyByQuery(String propertyName,
      DTOQuery<P, K> propertyDTOQuery) {

    this.propertyQueries.put(propertyName, propertyDTOQuery);
    return this;
  }

  public Collection<T> queryDTO() {
    SQLQuery<T> query = this.queryGenerator.apply(Collections.emptyList());
    List<T> resultSet = query.fetch();

    queryDTOProperties(resultSet);

    return resultSet;
  }

  protected void queryDTOProperties(Collection<T> resultSet) {

    for (Entry<String, DTOQuery<?, ?>> propQueryEntry : this.propertyQueries.entrySet()) {
      String propertyName = propQueryEntry.getKey();
      DTOQuery<?, ?> propDTOQuery = propQueryEntry.getValue();

      queryDTOProperty(resultSet, propertyName, propDTOQuery);
    }

  }

  protected void queryDTOProperty(Collection<T> resultSet, String propertyName,
      DTOQuery<?, ?> propDTOQuery) {

    propDTOQuery.queryGenerator.apply(resultSet);
    // TODO Auto-generated method stub

  }

}
