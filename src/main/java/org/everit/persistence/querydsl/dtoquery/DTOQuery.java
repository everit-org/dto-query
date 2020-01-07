package org.everit.persistence.querydsl.dtoquery;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.querydsl.sql.SQLQuery;

/**
 * Queries a DTO class.
 */
public class DTOQuery<FK, T> {

  public static <FK, T> DTOQuery<FK, T> byQuery(
      Function<Collection<FK>, SQLQuery<T>> queryGenerator) {

    return new DTOQuery<>(queryGenerator);
  }

  protected List<PropertyQuery<T, ?, ?>> propertyQueries = new ArrayList<>();

  protected Function<Collection<FK>, SQLQuery<T>> queryGenerator;

  public DTOQuery(Function<Collection<FK>, SQLQuery<T>> queryGenerator) {
    this.queryGenerator = queryGenerator;
  }

  public <P, K> DTOQuery<FK, T> prop(PropertyQuery<T, ?, ?> propertyQuery) {

    this.propertyQueries.add(propertyQuery);
    return this;
  }

  public Collection<T> queryDTO(Connection connection, Collection<FK> foreignKeys) {
    SQLQuery<T> query = this.queryGenerator.apply(foreignKeys);
    List<T> resultSet = query.clone(connection).fetch();

    queryDTOProperties(connection, resultSet);

    return resultSet;
  }

  protected void queryDTOProperties(Connection connection, Collection<T> resultSet) {

    for (PropertyQuery<T, ?, ?> propertyQuery : this.propertyQueries) {
      queryDTOProperty(connection, resultSet, propertyQuery);
    }
  }

  protected <PFK, P> void queryDTOProperty(Connection connection, Collection<T> dtos,
      PropertyQuery<T, PFK, P> propertyQuery) {

    Map<PFK, T> dtosByForeignKeys = new HashMap<>();
    for (T result : dtos) {
      Function<T, PFK> keyInSourceResolver = propertyQuery.keyInSourceResolver;
      PFK keyInSource = keyInSourceResolver.apply(result);
      dtosByForeignKeys.put(keyInSource, result);
    }

    Collection<P> properties =
        propertyQuery.dtoQuery.queryDTO(connection, dtosByForeignKeys.keySet());

    Map<PFK, Collection<P>> propertyCollectionByForeignKeyMap = new HashMap<>();
    Function<P, PFK> keyInPropertyResolver = propertyQuery.keyInPropertyResolver;
    for (P prop : properties) {
      PFK propertyForeignKey = keyInPropertyResolver.apply(prop);
      propertyCollectionByForeignKeyMap.computeIfAbsent(propertyForeignKey, k -> new ArrayList<>())
          .add(prop);
    }

    setPropertiesInDTOs(dtosByForeignKeys, propertyCollectionByForeignKeyMap, propertyQuery);
  }

  protected <PFK, P> void setPropertiesInDTOs(Map<PFK, T> dtosByForeignKeys,
      Map<PFK, Collection<P>> propertyCollectionByForeignKeyMap,
      PropertyQuery<T, PFK, P> propertyQuery) {

    for (Entry<PFK, T> dtoEntry : dtosByForeignKeys.entrySet()) {
      PFK foreignKey = dtoEntry.getKey();
      T dto = dtoEntry.getValue();

      if (dto == null) {
        throw new NullPointerException("No DTO found for foreign key: " + foreignKey.toString());
      }

      Collection<P> propertyCollection = propertyCollectionByForeignKeyMap.get(foreignKey);
      if (propertyCollection != null) {
        if (propertyQuery.setter != null) {
          // There is at least one element at this point otherwise the propertyCollection would be
          // null
          if (propertyCollection.size() > 1) {
            throw new IllegalArgumentException(
                "More than one property found for foreign key: " + foreignKey);
          }

          P prop = propertyCollection.iterator().next();
          propertyQuery.setter.accept(dto, prop);
        } else if (propertyQuery.oneToManySetter != null) {
          propertyQuery.oneToManySetter.accept(dto, propertyCollection);
        } else {
          throw new IllegalArgumentException(
              "No setter defined for property query: " + propertyQuery.toString());
        }
      }
    }

  }

}
