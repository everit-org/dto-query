package org.everit.persistence.querydsl.dtoquery;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyQuery<T, FK, P> {

  protected DTOQuery<FK, P> dtoQuery;

  protected Function<P, FK> keyInPropertyResolver;

  protected Function<T, FK> keyInSourceDTOResolver;

  protected BiConsumer<T, Collection<P>> oneToManySetter;

  protected BiConsumer<T, P> setter;

  public PropertyQuery<T, FK, P> dtoQuery(DTOQuery<FK, P> dtoQuery) {
    this.dtoQuery = dtoQuery;
    return this;
  }

  public PropertyQuery<T, FK, P> keyInPropertyDTOResolver(Function<P, FK> keyInPropertyResolver) {
    this.keyInPropertyResolver = keyInPropertyResolver;
    return this;
  }

  public PropertyQuery<T, FK, P> keyInSourceDTOResolver(Function<T, FK> keyInSourceResolver) {
    this.keyInSourceDTOResolver = keyInSourceResolver;
    return this;
  }

  public PropertyQuery<T, FK, P> oneToManySetter(BiConsumer<T, Collection<P>> oneToManySetter) {
    if (this.setter != null) {
      throw new IllegalArgumentException(
          "Setter and oneToManySetter cannot be specified at the same time");
    }
    this.oneToManySetter = oneToManySetter;
    return this;

  }

  public PropertyQuery<T, FK, P> setter(BiConsumer<T, P> setter) {
    if (this.oneToManySetter != null) {
      throw new IllegalArgumentException(
          "Setter and oneToManySetter cannot be specified at the same time");
    }
    this.setter = setter;
    return this;
  }
}
