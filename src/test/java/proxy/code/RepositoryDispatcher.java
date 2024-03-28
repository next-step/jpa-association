package proxy.code;

import net.sf.cglib.proxy.Dispatcher;

public class RepositoryDispatcher implements Dispatcher {

    private final String repositoryQualifier;

    public RepositoryDispatcher(final String repositoryQualifier) {
        this.repositoryQualifier = repositoryQualifier;
    }

    @Override
    public Object loadObject() throws Exception {
        if (this.repositoryQualifier.equalsIgnoreCase(ItemRepository.class.getSimpleName())) {
            return new ItemRepository();
        }
        return new OrderRepository();
    }
}
