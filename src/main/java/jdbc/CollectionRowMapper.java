package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaEntity;
import persistence.meta.Relation;

public class CollectionRowMapper<T> implements RowMapper<T> {

  public static String DELIMITER = ".";
  private final MetaEntity<T> metaEntity;

  public CollectionRowMapper(MetaEntity<T> metaEntity) {
    this.metaEntity = metaEntity;
  }

  @Override
  public T mapRow(ResultSet resultSet) throws SQLException {
    T entityInstance = metaEntity.createInstance();
    MetaDataColumns metaDataColumns = metaEntity.getMetaDataColumns();

    for (MetaDataColumn metaDataColumn : metaDataColumns.getMetaDataColumns()) {
      String columnName = String.join(DELIMITER, metaEntity.getTableName(),
          metaDataColumn.getDBColumnName());
      metaDataColumn.setFieldValue(entityInstance, resultSet.getObject(columnName));
    }

    Relation relation = metaDataColumns.getRelation();
    MetaDataColumn relationColumn = metaEntity.getMetaDataColumns()
        .getColumnByFieldName(relation.getFieldName());

    MetaEntity<?> elementEntity = metaEntity.getRelation().getMetaEntity();

    List<Object> elements = new ArrayList<>();

    do {
      Object elementInstance = elementEntity.createInstance();
      MetaDataColumns elementDataColumns = elementEntity.getMetaDataColumns();

      for (MetaDataColumn metaDataColumn : elementDataColumns.getMetaDataColumns()) {
        String columnName = String.join(DELIMITER, elementEntity.getTableName(),
            metaDataColumn.getDBColumnName());
        metaDataColumn.setFieldValue(elementInstance, resultSet.getObject(columnName));
      }
      elements.add(elementInstance);
    }
    while (resultSet.next());

    relationColumn.setFieldValue(entityInstance, elements);

    return entityInstance;
  }

}
