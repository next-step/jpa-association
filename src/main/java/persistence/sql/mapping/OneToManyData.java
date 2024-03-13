package persistence.sql.mapping;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class OneToManyData {
    private String joinColumnName;
    private TableData referenceTable;
    private FetchType fetchType;

    private OneToManyData(String joinColumnName, TableData referenceTable, FetchType fetchType) {
        this.joinColumnName = joinColumnName;
        this.referenceTable = referenceTable;
        this.fetchType = fetchType;
    }

    public static OneToManyData from(Field field) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Class<?> referenceClazz = (Class<?>) genericType.getActualTypeArguments()[0];

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

        return new OneToManyData(
                joinColumn.name(),
                TableData.from(referenceClazz),
                oneToMany.fetch()
        );
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    public String getJoinTableName() {
        return referenceTable.getName();
    }
}
