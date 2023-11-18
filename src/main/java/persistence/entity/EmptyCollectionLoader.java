package persistence.entity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EmptyCollectionLoader<T> implements RelationLoader<T>{

  @Override
  public Optional<T> load(Long id) {
    return Optional.empty();
  }

  @Override
  public List<T> loadByIds(List<Long> ids) {
    return Collections.emptyList();
  }
}
