package persistence.sql.dml;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import persistence.association.OneToManyAssociation;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;
import persistence.sql.QueryBuilder;

public class OneToManyJoinQueryBuilder extends QueryBuilder {
    public OneToManyJoinQueryBuilder(EntityMeta entityMeta) {
        super(entityMeta);
        if (!entityMeta.hasOneToManyAssociation()) {
            throw new IllegalArgumentException("해당 엔티티는 OneToMany 관계가 없습니다.");
        }
    }

    public String build() {
        return generateJoinQueryStream(entityMeta, 0)
                .collect(Collectors.joining(""));
    }

    private String generateJoinQuery(EntityMeta entityMeta, int depth) {
        if (!entityMeta.hasOneToManyAssociation()) {
            return "";
        }
        final OneToManyAssociation oneToManyAssociate = entityMeta.getOneToManyAssociation();
        final EntityColumn joinPkColumn = oneToManyAssociate.getPkManyColumn();

        final String drivenTableSignature = tableNameSignature(entityMeta.getTableName(), depth);
        final String joinTableSignature = tableNameSignature(oneToManyAssociate.getManyEntityMeta().getTableName(), depth + 1);

        return dialect.leftJoin(oneToManyAssociate.getManyEntityMeta().getTableName(), joinTableSignature) +
                columnSignature(drivenTableSignature, joinPkColumn.getName()) + " = " + columnSignature(joinTableSignature, oneToManyAssociate.foreignerColumnName());
    }

    private Stream<String> generateJoinQueryStream(EntityMeta entityMeta, int depth) {
        Stream<String> joinString = Stream.of(generateJoinQuery(entityMeta, depth));
        if (entityMeta.hasOneToManyAssociation()) {
            return Stream.concat(joinString,
                    generateJoinQueryStream(entityMeta.getOneToManyAssociation().getManyEntityMeta(), depth + 1));
        }
        return joinString;
    }

}
