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

    public <T> JoinEntity(T entityInstance) {
        this.joinStatus = JoinStatus.FALSE;
        getInstanceColumnData(entityInstance);
    }

    public JoinEntity(Class<?> clazz) {
        this.joinStatus = JoinStatus.FALSE;
        getEntityColumnData(clazz);
    }

    public List<JoinEntityData> getJoinEntityData() {
        return joinEntityData;
    }

    public boolean checkJoin() {
        return this.joinStatus.isTrue();
    }

    private <T> void getInstanceColumnData(T entityInstance) {
        for (Field field : entityInstance.getClass().getDeclaredFields()) {
            createDMLInstanceColumnData(field, entityInstance);
        }
    }

    private void getEntityColumnData(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            createDMLEntityColumnData(field);
        }
    }

    private <T> void createDMLInstanceColumnData(Field field, T entityInstance) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            FetchType fetchType = oneToMany.fetch();

            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            field.setAccessible(true);
            List<?> relatedEntities;
            try {
                relatedEntities = (List<?>) field.get(entityInstance); // 부모 객체에서 List를 가져옴
            } catch (IllegalAccessException e) {
                throw new RuntimeException(GET_FIELD_VALUE_ERROR_MESSAGE + field.getName(), e);
            }

            relatedEntities.forEach(object -> {
                        this.joinEntityData.add(new JoinEntityData(fetchType, object, joinColumn.name()));
                        joinStatus = JoinStatus.TRUE;
                    });
        }
    }

    private void createDMLEntityColumnData(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            FetchType fetchType = oneToMany.fetch();

            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

            Type type = field.getGenericType();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            this.joinEntityData.add(new JoinEntityData(fetchType, (Class<?>) types[0], joinColumn.name()));
            joinStatus = JoinStatus.TRUE;
        }
    }
}
