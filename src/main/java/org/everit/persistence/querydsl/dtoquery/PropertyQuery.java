package org.everit.persistence.querydsl.dtoquery;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyQuery<T, FK, P> {

  private DTOQuery<FK, P> dtoQuery;

  private Function<P, FK> keyInPropertyResolver;

  private Function<T, FK> keyInSourceResolver;

  private BiConsumer<T, Collection<P>> oneToManySetter;

  private BiConsumer<T, P> setter;

  public PropertyQuery<T, FK, P> dtoQuery(DTOQuery<FK, P> dtoQuery) {
    this.dtoQuery = dtoQuery;
    return this;
  }

  public DTOQuery<FK, P> getDtoQuery() {
    return this.dtoQuery;
  }

  public Function<P, FK> getKeyInPropertyResolver() {
    return this.keyInPropertyResolver;
  }

  public Function<T, FK> getKeyInSourceResolver() {
    return this.keyInSourceResolver;
  }

  public BiConsumer<T, Collection<P>> getOneToManySetter() {
    return this.oneToManySetter;
  }

  public BiConsumer<T, P> getSetter() {
    return this.setter;
  }

  public PropertyQuery<T, FK, P> keyInPropertyResolver(Function<P, FK> keyInPropertyResolver) {
    this.keyInPropertyResolver = keyInPropertyResolver;
    return this;
  }

  public PropertyQuery<T, FK, P> keyInSourceResolver(Function<T, FK> keyInSourceResolver) {
    this.keyInSourceResolver = keyInSourceResolver;
    return this;
  }

  public PropertyQuery<T, FK, P> oneToManySetter(BiConsumer<T, Collection<P>> oneToManySetter) {
    this.oneToManySetter = oneToManySetter;
    return this;

  }

  public PropertyQuery<T, FK, P> setter(BiConsumer<T, P> setter) {
    this.setter = setter;
    return this;
  }
}
