package persistence.sql.dml;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;


public class SelectQueryBuilder extends DMLQueryBuilder {
    public SelectQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }

    public String findAllQuery() {
        return selectQuery(selectColumn(entityMeta).collect(Collectors.joining(", ")))
                + getFromTableQuery(entityMeta.getTableName(), tableNameSignature(entityMeta.getTableName()));
    }

    public String findByIdQuery(Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id가 비어 있으면 안 됩니다.");
        }

        return selectQuery(selectColumn(entityMeta).collect(Collectors.joining(", ")))
                + getFromTableQuery(entityMeta.getTableName(), tableNameSignature(entityMeta.getTableName()))
                + whereId(tableNameSignature(entityMeta.getTableName()), getPkColumn(), id);
    }


    public String findByForeignerId(Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id가 비어 있으면 안 됩니다.");
        }
        final EntityMeta manyEntityMeta = entityMeta
                .getOneToManyAssociation()
                .getManyEntityMeta();


        return selectQuery(getColumnsString(manyEntityMeta))
                + getFromTableQuery(manyEntityMeta.getTableName(), tableNameSignature(manyEntityMeta.getTableName()))
                + whereId(tableNameSignature(manyEntityMeta.getTableName()), manyEntityMeta.getForeignerColumn(), id);
    }

    public String findAllOneToManyQuery() {
        return selectQuery(getColumnsString(entityMeta))
                + getFromTableQuery(entityMeta.getTableName(), tableNameSignature(entityMeta.getTableName()))
                + new OneToManyJoinQueryBuilder(entityMeta).build();

    }

    public String findByIdOneToManyQuery(Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id가 비어 있으면 안 됩니다.");
        }

        return selectQuery(getColumnsString(entityMeta))
                + getFromTableQuery(entityMeta.getTableName(), tableNameSignature(entityMeta.getTableName()))
                + new OneToManyJoinQueryBuilder(entityMeta).build()
                + whereId(tableNameSignature(entityMeta.getTableName()), getPkColumn(), id);
    }


    private String selectQuery(String fileNames) {
        return dialect.select(fileNames);
    }

    private String getColumnsString(EntityMeta entityMeta) {
        return getColumnsStream(entityMeta, 0)
                .collect(Collectors.joining(", "));

    }

    private Stream<String> selectColumn(EntityMeta entityMeta) {
        return convertColumnsSegnetureStream(entityMeta, 0);
    }

    private Stream<String> convertColumnsSegnetureStream(EntityMeta entityMeta, int depth) {
        final String tableName = entityMeta.getTableName()+ "_"  + depth;

        Stream<String> columns = entityMeta.getEntityColumns()
                .stream()
                .map((it) -> generateColumString(tableName, it.getName()));

        if (!entityMeta.hasForeignerColumn()) {
            return columns;
        }

        return Stream.concat(Stream.of(generateColumString(tableName, entityMeta.getForeignerColumn().getName())), columns);
    }

    private Stream<String> getColumnsStream(EntityMeta entityMeta, int depth) {
        final Stream<String> columnsNameStream = convertColumnsSegnetureStream(entityMeta, depth);

        if (entityMeta.hasOneToManyAssociation()) {
            return Stream.concat(columnsNameStream, getColumnsStream(entityMeta.getOneToManyAssociation().getManyEntityMeta(),depth + 1));
        }

        return columnsNameStream;
    }

    private static String generateColumString(String tableName, String columnName) {
        return tableName + "." + columnName + " as " + tableName + "_" + columnName;
    }


}
