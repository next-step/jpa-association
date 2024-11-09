package persistence.sql.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import persistence.sql.model.EntityId;
import persistence.sql.model.TableName;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityJoin {

    private final List<EntityJoinInfo> entityJoinInfos = new ArrayList<>();
    private final EntityId entityId;

    public EntityJoin(Class<?> clazz) {
        addEntityJoinInfos(clazz);
        this.entityId = new EntityId(clazz);
    }

    public void addEntityJoinInfos(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(OneToMany.class))
                .forEach(field -> {
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

                    Type genericType = field.getGenericType();
                    Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                    entityJoinInfos.add(
                            new EntityJoinInfo(
                                    (Class<?>) types[0],
                                    clazz,
                                    new EntityId(clazz).getIdColumnName(),
                                    oneToMany.fetch(),
                                    joinColumn.name(),
                                    field
                            )
                    );
        });
    }

    public boolean isEntityJoin() {
        return !entityJoinInfos.isEmpty();
    }

    public String makeJoinTableQuery() {
        StringBuilder joinQueryBuilder = new StringBuilder();
        entityJoinInfos.forEach(entityJoinInfo -> {
            joinQueryBuilder.append("LEFT JOIN ");
            joinQueryBuilder.append(entityJoinInfo.getJoinTableName());
            joinQueryBuilder.append(" ON ");
            joinQueryBuilder.append(new TableName(entityJoinInfo.getClazz()).getAlias());
            joinQueryBuilder.append(".");
            joinQueryBuilder.append(entityId.getIdColumnName());
            joinQueryBuilder.append(" = ");
            joinQueryBuilder.append(new TableName(entityJoinInfo.getJoinClazz()).getAlias());
            joinQueryBuilder.append(".");
            joinQueryBuilder.append(entityJoinInfo.getJoinColumnName());
        });
        return joinQueryBuilder.toString();
    }

    public List<Class<?>> getJoinClasses() {
        return this.entityJoinInfos.stream().map(EntityJoinInfo::getJoinClazz).collect(Collectors.toList());
    }

    public List<EntityJoinInfo> getEntityJoinInfos() {
        return entityJoinInfos;
    }
}
