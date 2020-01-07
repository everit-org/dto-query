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
