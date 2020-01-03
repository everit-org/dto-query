package org.everit.persistence.querydsl.dtoquery;

import java.util.Collection;
import java.util.Map;

public interface DTOPropertyResolver<R, P> {

  Map<R, P> resolvePropertyByRefererMap(Collection<R> referers);

}
