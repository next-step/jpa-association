package persistence.meta.store;

import java.util.List;
import persistence.meta.ColumnMeta;
import persistence.meta.ColumnValueMeta;

public class ObjectValueMetas {

    public static List<Object> values(List<ColumnMeta> columnMetas, Object instance) {
        return columnMetas.stream()
                .map(columnMeta -> ColumnValueMeta.of(columnMeta.field(), instance))
                .map(ColumnValueMeta::value)
                .toList();
    }

    public static List<Object> valuesWithoutPrimaryKey(List<ColumnMeta> columnMetas, Object instance) {
        return columnMetas.stream()
                .filter(ColumnMeta::isNotPrimaryKey)
                .map(columnMeta -> ColumnValueMeta.of(columnMeta.field(), instance))
                .map(ColumnValueMeta::value)
                .toList();
    }

}
