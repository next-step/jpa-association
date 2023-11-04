package extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import persistence.Application;
import persistence.core.EntityMetadataProvider;
import persistence.entity.EntityScanner;

public class EntityMetadataExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(final ExtensionContext context) {
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        final EntityMetadataProvider entityMetadataProvider = EntityMetadataProvider.getInstance();
        entityMetadataProvider.init(entityScanner);
    }
}
