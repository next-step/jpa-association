package persistence.sql.dml;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import persistence.association.OneToManyAssociation;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;


public class SelectQueryBuilder extends DMLQueryBuilder {
    public SelectQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }


    public String findAllQuery() {
        if (entityMeta.hasOneToManyAssociate()) {
            return selectQuery(getColumnsString(entityMeta))
                    + getFromTableQuery(entityMeta.getTableName(), tableNameSignature(entityMeta.getTableName()))
                    + new OneToManyJoinQueryBuilder(entityMeta).build();
        }

        return selectQuery(getColumnsString(entityMeta))
                + getFromTableQuery(entityMeta.getTableName(), tableNameSignature(entityMeta.getTableName()));
    }

    public String findByIdQuery(Object id) {
        if (id == null) {
            throw new IllegalArgumentException("id가 비어 있으면 안 됩니다.");
        }
        if (entityMeta.hasOneToManyAssociate()) {
            return findAllQuery()
                    + whereId(tableNameSignature(entityMeta.getTableName()) ,getPkColumn(), id);
        }

        return findAllQuery()
                + whereId(tableNameSignature(entityMeta.getTableName()) ,getPkColumn(), id);
    }

    private String selectQuery(String fileNames) {
        return dialect.select(fileNames);
    }

    private String getColumnsString(EntityMeta entityMeta) {
        return getColumnsStream(entityMeta, 0)
                .collect(Collectors.joining(", "));

    }

    private Stream<String> convertColumnsSegnetureStream(EntityMeta entityMeta, int depth) {
        final String tableName = entityMeta.getTableName()+ "_"  + depth;

        Stream<String> columns = entityMeta.getEntityColumns()
                .stream()
                .map((it) -> generateColumString(tableName, it.getName()));

        if (!entityMeta.hasOneToManyAssociate()) {
            return columns;
        }
        OneToManyAssociation oneToManyAssociate = entityMeta.getOneToManyAssociation();

        return Stream.concat(columns,
                Stream.of(generateColumString(tableName, oneToManyAssociate.joinColumnName()))
        );
    }

    private Stream<String> getColumnsStream(EntityMeta entityMeta, int depth) {
        final Stream<String> columnsNameStream = convertColumnsSegnetureStream(entityMeta, depth);

        if (entityMeta.hasOneToManyAssociate()) {
            return Stream.concat(columnsNameStream, getColumnsStream(entityMeta.getOneToManyAssociation().getManyEntityMeta(),depth + 1));
        }

        return columnsNameStream;
    }

    private static String generateColumString(String tableName, String columnName) {
        return tableName + "." + columnName + " as " + tableName + "_" + columnName;
    }


}
