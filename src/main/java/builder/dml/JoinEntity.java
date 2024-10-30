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

    private final List<JoinEntityData> joinEntityData = new ArrayList<>();
    private JoinStatus joinStatus;

    public JoinEntity() {
        this.joinStatus = JoinStatus.FALSE;
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

    private void getEntityColumnData(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            createDMLEntityColumnData(field);
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
