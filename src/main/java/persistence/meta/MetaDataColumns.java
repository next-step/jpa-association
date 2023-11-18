package persistence.meta;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;

public class MetaDataColumns {

  public static final String DELIMITER = ",";
  private final List<MetaDataColumn> columns = new ArrayList<>();
  private final MetaDataColumn primaryColumn;
  private final Relation relation;
  private MetaDataColumns(List<MetaDataColumn> metaColumns, MetaDataColumn primaryColumn,
      Relation relation) {
    this.primaryColumn = primaryColumn;
    this.relation = relation;
    columns.addAll(metaColumns);
  }

  public static MetaDataColumns of(Class<?> clazz, Dialect dialect) {
    List<MetaDataColumn> metaColumns = Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> isNotTransient(List.of(field.getAnnotations())))
        .map(field -> MetaDataColumn.of(field, dialect.convertToColumn(field)))
        .collect(Collectors.toList());

    MetaDataColumn primaryKeyColumn = metaColumns.stream()
        .filter(column -> !column.isNotPrimaryKey())
        .findFirst().orElseThrow(() -> new RuntimeException("ID 필드가 없습니다."));

    Relation relation = metaColumns.stream()
        .filter(column -> column.hasRelation())
        .map(column -> column.getColumnRelation())
        .findAny().orElse(new MetaDataColumnEmptyRelation());

    return new MetaDataColumns(metaColumns, primaryKeyColumn, relation);
  }

  public String getColumns() {
    return columns.stream()
        .filter(column -> !column.hasRelation())
        .map(MetaDataColumn::getDBColumnsClause)
        .collect(Collectors.joining(DELIMITER));
  }

  private static boolean isNotTransient(List<Annotation> annotations) {
    return annotations.stream()
        .noneMatch(annotation -> annotation.annotationType().equals(Transient.class));
  }

  private static boolean isNotRelation(List<Annotation> annotations) {
    return annotations.stream()
        .noneMatch(annotation -> annotation.annotationType().equals(OneToMany.class));
  }

  public List<String> getDBColumnsWithoutId() {
    return columns.stream()
        .filter(MetaDataColumn::isNotPrimaryKey)
        .filter(column -> !column.hasRelation())
        .map(MetaDataColumn::getDBColumnName)
        .collect(Collectors.toList());
  }
  public List<String> getFields() {
    return columns.stream()
        .filter(MetaDataColumn::isNotPrimaryKey)
        .map(MetaDataColumn::getFieldName)
        .collect(Collectors.toList());
  }

  public List<String> getColumnsWithId() {
    return columns.stream()
        .filter(column -> !column.hasRelation())
        .map(MetaDataColumn::getDBColumnName)
        .collect(Collectors.toList());
  }

  public boolean hasRelation(){
    return relation.isRelation();
  }
  public Relation getRelation(){
    return relation;
  }

  public MetaDataColumn getPrimaryColumn() {
    return primaryColumn;
  }

  public List<MetaDataColumn> getMetaDataColumns() {
    return columns.stream().filter(column -> !column.hasRelation()).collect(Collectors.toList());
  }

  public MetaDataColumn getColumnByFieldName(String fieldName) {
    return columns.stream().filter(column -> column.getFieldName().equals(fieldName)).findFirst()
        .orElseThrow(() -> new RuntimeException("해당 field가 없습니다."));
  }
}
