package persistence.sql.entity.model;

import java.lang.reflect.ParameterizedType;

public class SubEntityType {

    private static final int ZERO = 0;

    private final DomainType domainType;

    public SubEntityType(final DomainType domainType) {
        this.domainType = domainType;
    }

    public Class<?> getSubClass() {
        ParameterizedType parameterizedType = (ParameterizedType) domainType.getField().getGenericType();
        return (Class<?>) parameterizedType.getActualTypeArguments()[ZERO];
    }
}
