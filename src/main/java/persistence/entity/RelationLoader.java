package persistence.entity;

import java.util.List;
import java.util.Optional;

public interface RelationLoader<T> {

  <T> Optional<T> load(Long id);

  List<T> loadByIds(List<Long> ids);
}
