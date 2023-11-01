package persistence.dialect;

public interface PagingStrategy {
    String renderPagingQuery(final PageQuery pageQuery);

}
