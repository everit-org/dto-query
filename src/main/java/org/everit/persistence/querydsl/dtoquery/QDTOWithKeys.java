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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.SimpleExpression;

/**
 * Helper class to select a DTO and additional fields from the database as Tuple. This can be useful
 * if the selected property DTO or the source DTO class would not contain the foreign key.
 *
 * @param <T>
 *          The type of the DTO.
 */
public class QDTOWithKeys<T>
    implements Expression<DTOWithKeys<T>>, FactoryExpression<DTOWithKeys<T>> {

  private static final long serialVersionUID = -8947639515013490017L;

  private final List<Expression<?>> args;

  private final FactoryExpression<T> beanExpression;

  private final QTuple keysQTuple;

  private final SimpleExpression<T> simpleExpression;

  /**
   * Constructor.
   *
   * @param beanExpression
   *          The expression to select the DTO. Probably created with Projections.fields() or
   *          Projections.bean() method.
   * @param keys
   *          The additional keys that should be selected as a Tuple.
   */
  public QDTOWithKeys(FactoryExpression<T> beanExpression, Expression<?>... keys) {
    this.beanExpression = beanExpression;
    this.simpleExpression = null;

    this.keysQTuple = Projections.tuple(keys);

    ArrayList<Expression<?>> args = new ArrayList<>(beanExpression.getArgs());
    args.addAll(Arrays.asList(keys));
    this.args = Collections.unmodifiableList(args);
  }

  /**
   * Constructor.
   *
   * @param simpleExpression
   *          The expression to select a simple value.
   * @param keys
   *          The additional keys that should be selected as a Tuple.
   */
  public QDTOWithKeys(SimpleExpression<T> simpleExpression, Expression<?>... keys) {
    this.beanExpression = null;
    this.simpleExpression = simpleExpression;

    this.keysQTuple = Projections.tuple(keys);
    List<Expression<?>> args = new ArrayList<>();
    args.add(simpleExpression);
    args.addAll(Arrays.asList(keys));
    this.args = Collections.unmodifiableList(args);
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
  }

  @Override
  public List<Expression<?>> getArgs() {
    return this.args;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<? extends DTOWithKeys<T>> getType() {
    return (Class<? extends DTOWithKeys<T>>) (Object) DTOWithKeys.class;
  }

  @Override
  public DTOWithKeys<T> newInstance(Object... args) {

    int beanPropCount = this.beanExpression != null ? this.beanExpression.getArgs().size() : 1;

    Object[] argsOfDTO = Arrays.copyOf(args, beanPropCount);

    @SuppressWarnings("unchecked")
    T dto = this.beanExpression != null ? this.beanExpression.newInstance(argsOfDTO) : (T) args[0];

    Tuple keys = this.keysQTuple.newInstance(Arrays.copyOfRange(args, beanPropCount, args.length));

    DTOWithKeys<T> result = new DTOWithKeys<>();
    result.dto = dto;
    result.keys = keys;

    return result;
  }

  @Override
  public String toString() {
    return "QDTOWithKeys [simpleExpression=" + this.simpleExpression + ", beanExpression="
        + this.beanExpression + ", args=" + this.args + ", keysQTuple=" + this.keysQTuple + "]";
  }

}
