package proxy.code;

public class Service {
    private final Repository itemRepository;

    public Service(final Repository repository) {
        this.itemRepository = repository;
    }

    public String getName() {
        return this.itemRepository.getName();
    }
}
