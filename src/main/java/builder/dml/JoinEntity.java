package builder.dml;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JoinEntity {

    private static final String GET_FIELD_VALUE_ERROR_MESSAGE = "필드 값을 가져오는 중 에러가 발생했습니다.";
    private final List<JoinEntityData> joinEntityData = new ArrayList<>();
    private JoinStatus joinStatus;
    private FetchType fetchType;

    public <T> JoinEntity(T entityInstance, Object joinColumnValue) {
        this.joinStatus = JoinStatus.FALSE;
        getInstanceColumnData(entityInstance, joinColumnValue);
    }

    public JoinEntity(Class<?> clazz, Object joinColumnValue) {
        this.joinStatus = JoinStatus.FALSE;
        getEntityColumnData(clazz, joinColumnValue);
    }

    public List<JoinEntityData> getJoinEntityData() {
        return joinEntityData;
    }

    public boolean checkJoin() {
        return this.joinStatus.isTrue();
    }

    public boolean checkFetchEager() {
        return this.fetchType == FetchType.EAGER;
    }

    public JoinEntityData findJoinEntityData(Class<?> clazz) {
        return this.joinEntityData.stream()
                .filter(entityData -> entityData.getClazz() == clazz)
                .findFirst()
                .orElse(null);
    }

    private void changeFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }

    private <T> void getInstanceColumnData(T entityInstance, Object joinColumnValue) {
        for (Field field : entityInstance.getClass().getDeclaredFields()) {
            createDMLInstanceColumnData(field, entityInstance, joinColumnValue);
        }
    }

    private void getEntityColumnData(Class<?> entityClass, Object joinColumnName) {
        for (Field field : entityClass.getDeclaredFields()) {
            createDMLEntityColumnData(field, joinColumnName);
        }
    }

    private <T> void createDMLInstanceColumnData(Field field, T entityInstance, Object joinColumnValue) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            changeFetchType(oneToMany.fetch());


            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            field.setAccessible(true);
            List<?> relatedEntities;
            try {
                relatedEntities = (List<?>) field.get(entityInstance); // 부모 객체에서 List를 가져옴
            } catch (IllegalAccessException e) {
                throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
            }

            relatedEntities.forEach(object -> {
                this.joinEntityData.add(new JoinEntityData(object, joinColumn.name(), joinColumnValue));
                joinStatus = JoinStatus.TRUE;
            });
        }
    }

    private void createDMLEntityColumnData(Field field, Object joinColumnValue) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            changeFetchType(oneToMany.fetch());

            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

            Type type = field.getGenericType();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            this.joinEntityData.add(new JoinEntityData((Class<?>) types[0], joinColumn.name(), joinColumnValue));
            joinStatus = JoinStatus.TRUE;
        }
    }
}
