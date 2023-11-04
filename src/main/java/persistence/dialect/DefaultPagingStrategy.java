package persistence.dialect;

public class DefaultPagingStrategy implements PagingStrategy {

    private DefaultPagingStrategy() {
    }

    public static DefaultPagingStrategy getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String renderPagingQuery(final PageQuery pageQuery) {
        final StringBuilder pagingBuilder = new StringBuilder();
        pagingBuilder.append(pageQuery.getQuery())
                .append(" limit ")
                .append(pageQuery.getLimit());
        if (pageQuery.shouldRenderOffset()) {
            pagingBuilder.append(" offset ")
                    .append(pageQuery.getOffset());
        }
        return pagingBuilder.toString();
    }

    private static class InstanceHolder {
        private static final DefaultPagingStrategy INSTANCE = new DefaultPagingStrategy();
    }
}
