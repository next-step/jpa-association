package persistence.dialect;

public class RownumPagingStrategy implements PagingStrategy {

    private RownumPagingStrategy() {
    }

    public static RownumPagingStrategy getInstance() {
        return RownumPagingStrategy.InstanceHolder.INSTANCE;
    }

    @Override
    public String renderPagingQuery(final PageQuery pageQuery) {
        final StringBuilder pagingBuilder = new StringBuilder();
        pagingBuilder
                .append("select * from (select row.*, rownum as rnum from (")
                .append(pageQuery.getQuery())
                .append(") row) where rnum > ")
                .append(pageQuery.getOffset())
                .append(" and rnum <= ")
                .append(pageQuery.getMaxResult());
        return pagingBuilder.toString();
    }

    private static class InstanceHolder {
        private static final RownumPagingStrategy INSTANCE = new RownumPagingStrategy();
    }

}

