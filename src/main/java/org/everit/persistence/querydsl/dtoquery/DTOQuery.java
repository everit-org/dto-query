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
package org.everit.persistence.querydsl.dtoquery;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;

/**
 * Queries a DTO class.
 *
 * @param <T>
 *          Type of the DTO.
 * @param <FK>
 *          Type of the foreign key
 */
public class DTOQuery<FK, T> {

  /**
   * Helper method to create a DTOQuery using setters and selecting all fields from the table as is.
   *
   * @param <FK>
   *          The type of the foreign key if the dtoQuery is called as a property selector of
   *          another DTO.
   * @param <T>
   *          The type of the DTO.
   * @param configuration
   *          The Querydsl configuration.
   * @param qTable
   *          The Querydsl Q class of the table.
   * @param dtoType
   *          The type of the DTO that should be instantiated.
   * @param foreignKeyPath
   *          The path to the foreign key in the generated SQL.
   * @return The {@link DTOQuery} instance that can be further configured.
   */
  public static <FK, T> DTOQuery<FK, T> beanFullTable(Configuration configuration,
      RelationalPathBase<?> qTable, Class<T> dtoType, SimpleExpression<FK> foreignKeyPath) {

    return DTOQuery.create(
        (fks) -> new SQLQuery<T>(configuration).select(Projections.bean(dtoType, qTable.all()))
            .from(qTable).where(foreignKeyPath.in(fks)));
  }

  /**
   * Helper method to be able to write simpler code. If the constructor was used, the type
   * parameters should be defined. The same as calling {@link DTOQuery#queryGenerator(Function)} on
   * a newly created {@link DTOQuery} instance.
   */
  public static <FK, T> DTOQuery<FK, T> create(
      Function<Collection<FK>, SQLQuery<T>> queryGenerator) {

    return new DTOQuery<FK, T>().queryGenerator(queryGenerator);
  }

  /**
   * Helper method to create a DTOQuery using public member variable access and selecting all fields
   * from the table as is.
   *
   * @param <FK>
   *          The type of the foreign key if the dtoQuery is called as a property selector of
   *          another DTO.
   * @param <T>
   *          The type of the DTO.
   * @param configuration
   *          The Querydsl configuration.
   * @param qTable
   *          The Querydsl Q class of the table.
   * @param dtoType
   *          The type of the DTO that should be instantiated.
   * @param foreignKeyPath
   *          The path to the foreign key in the generated SQL.
   * @return The {@link DTOQuery} instance that can be further configured.
   */
  public static <FK, T> DTOQuery<FK, T> dtoFullTable(Configuration configuration,
      RelationalPathBase<?> qTable, Class<T> dtoType, SimpleExpression<FK> foreignKeyPath) {

    return DTOQuery.create(
        (fks) -> new SQLQuery<T>(configuration).select(Projections.fields(dtoType, qTable.all()))
            .from(qTable).where(foreignKeyPath.in(fks)));
  }

  /**
   * Helper method to create a {@link DTOQuery} for the root DTO type. In case of root DTO type one
   * does not need to handle incoming foreign keys.
   *
   * @param <T>
   *          The type of the DTOs that are queried from the database.
   * @param query
   *          The query that is used to select the DTOs.
   * @return A DTO query instance that can be further configured.
   */
  public static <T> DTOQuery<Object, T> root(SQLQuery<T> query) {
    return new DTOQuery<Object, T>().queryGenerator((fks) -> query);
  }

  /**
   * Helper method to create a DTOQuery using setters and selecting all fields from the table as is.
   * This function can be used to select the root DTOs.
   *
   * @param <T>
   *          The type of the DTO.
   * @param configuration
   *          The Querydsl configuration.
   * @param qTable
   *          The Querydsl Q class of the table.
   * @param dtoType
   *          The type of the DTO that should be instantiated.
   * @return The {@link DTOQuery} instance that can be further configured.
   */
  public static <T> DTOQuery<Object, T> rootBeanFullTable(
      Configuration configuration, RelationalPathBase<?> qTable,
      Class<T> dtoType) {

    return DTOQuery
        .root(new SQLQuery<>(configuration).select(Projections.bean(dtoType, qTable.all()))
            .from(qTable));
  }

  /**
   * Helper method to create a DTOQuery using public member variable access and selecting all fields
   * from the table as is. This function can be used to select the root DTOs.
   *
   * @param <T>
   *          The type of the DTO.
   * @param configuration
   *          The Querydsl configuration.
   * @param qTable
   *          The Querydsl Q class of the table.
   * @param dtoType
   *          The type of the DTO that should be instantiated.
   * @return The {@link DTOQuery} instance that can be further configured.
   */
  public static <T> DTOQuery<Object, T> rootDTOFullTable(
      Configuration configuration, RelationalPathBase<?> qTable,
      Class<T> dtoType) {

    return DTOQuery
        .root(new SQLQuery<>(configuration).select(Projections.fields(dtoType, qTable.all()))
            .from(qTable));
  }

  private List<PropertyQuery<T, ?, ?>> propertyQueries = new ArrayList<>();

  private Function<Collection<FK>, SQLQuery<T>> queryGenerator;

  /**
   * Adds a property query to this DTO so the given property will be queried after all DTOs by this
   * {@link DTOQuery} is selected.
   *
   * @param <P>
   *          Type of the property.
   * @param <K>
   *          Type of the key that connects the property and the DTOs selected by this
   *          {@link DTOQuery}.
   * @param propertyQuery
   *          The query of the property that must be built by the programmer. See
   *          {@link PropertyQuery}.
   * @return This {@link DTOQuery} instance.
   */
  public <P, K> DTOQuery<FK, T> prop(PropertyQuery<T, ?, ?> propertyQuery) {

    this.propertyQueries.add(propertyQuery);
    return this;
  }

  /**
   * Runs the query on the database. This function is suitable when the passed Querydsl query has
   * already a connection applied.
   *
   * @return Collection of DTOs.
   */
  public Collection<T> queryDTO() {
    return this.queryDTO(Optional.empty(), Collections.emptySet());
  }

  /**
   * Runs the query on the database. When using this function, the passed conncetion will be used
   * for database queries.
   *
   * @param connection
   *          The database connection.
   * @return Collection of DTOs.
   */
  public Collection<T> queryDTO(Connection connection) {
    return this.queryDTO(Optional.of(connection), Collections.emptySet());
  }

  /**
   * Queries the set of DTOs by filtering the specific foreign keys.
   *
   * @param connection
   *          The database connection.
   * @param foreignKeys
   *          The foreign keys to filter in the query.
   * @return The collection of DTOs that were pulled out from the database.
   */
  private Collection<T> queryDTO(Optional<Connection> connection, Collection<FK> foreignKeys) {
    SQLQuery<T> query = this.queryGenerator.apply(foreignKeys);
    query = connection.isPresent() ? query.clone(connection.get()) : query;
    List<T> resultSet = query.fetch();

    queryDTOProperties(connection, resultSet);

    return resultSet;
  }

  private void queryDTOProperties(Optional<Connection> connection, Collection<T> resultSet) {

    for (PropertyQuery<T, ?, ?> propertyQuery : this.propertyQueries) {
      queryDTOProperty(connection, resultSet, propertyQuery);
    }
  }

  private <PFK, P> void queryDTOProperty(Optional<Connection> connection, Collection<T> dtos,
      PropertyQuery<T, PFK, P> propertyQuery) {

    Map<PFK, T> dtosByForeignKeys = new HashMap<>();
    for (T result : dtos) {
      Function<T, PFK> keyInSourceResolver = propertyQuery.keyInSourceDTOResolver;
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

  /**
   * Specifying the query generator for this DTO.
   *
   * @param queryGenerator
   *          The generator of the SQLQuery of this {@link DTOQuery}. The generator is a function
   *          where the parameter is the list of foreign keys coming from the source DTO and the
   *          result is the generated SQLQuery.
   * @return This {@link DTOQuery} instance.
   */
  public DTOQuery<FK, T> queryGenerator(Function<Collection<FK>, SQLQuery<T>> queryGenerator) {
    this.queryGenerator = queryGenerator;
    return this;
  }

  private <PFK, P> void setPropertiesInDTOs(Map<PFK, T> dtosByForeignKeys,
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
