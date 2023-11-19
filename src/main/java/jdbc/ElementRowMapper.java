package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaEntity;

public class ElementRowMapper<T> implements CollectionRowMapper<T> {

  public static String DELIMITER = ".";
  private final MetaEntity<T> metaEntity;

  public ElementRowMapper(MetaEntity<T> metaEntity) {
    this.metaEntity = metaEntity;
  }

  @Override
  public List<T> mapRow(ResultSet resultSet) throws SQLException {
    List<T> elements = new ArrayList<>();

    do {
      T elementInstance = metaEntity.createInstance();
      MetaDataColumns elementDataColumns = metaEntity.getMetaDataColumns();

      for (MetaDataColumn metaDataColumn : elementDataColumns.getMetaDataColumns()) {
        String columnName = String.join(DELIMITER, metaEntity.getTableName(),
            metaDataColumn.getDBColumnName());
        metaDataColumn.setFieldValue(elementInstance, resultSet.getObject(columnName));
      }
      elements.add(elementInstance);
    }
    while (resultSet.next());

    return elements;
  }

}
