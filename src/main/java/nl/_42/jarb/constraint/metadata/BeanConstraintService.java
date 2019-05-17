package nl._42.jarb.constraint.metadata;

import nl._42.jarb.constraint.metadata.factory.EntityFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by maarten.hus on 22/05/15.
 */
public class BeanConstraintService {

    private final Set<Class<?>> beanTypes = new HashSet<>();

    private final BeanConstraintDescriptor descriptor;

    public BeanConstraintService(BeanConstraintDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Retrieves all entities registered.
     * @return the descriptions
     */
    public Map<String, Map<String, PropertyConstraintDescription>> describeAll() {
        Map<String, Map<String, PropertyConstraintDescription>> descriptions = new HashMap<>();
        for (Class<?> beanType : beanTypes) {
            Map<String, PropertyConstraintDescription> properties = describe(beanType);
            descriptions.put(getTypeName(beanType), properties);
        }
        return descriptions;
    }

    protected String getTypeName(Class<?> beanType) {
        return beanType.getSimpleName();
    }

    /**
     * Describes a certain entity.
     * @param entityType the entity type
     * @return the description
     */
    public Map<String, PropertyConstraintDescription> describe(String entityType) {
        BeanConstraintDescription description = descriptor.describeBean(entityType);
        return describe(description);
    }

    /**
     * Describes a certain entity.
     * @param entityClass the entity class
     * @return the description
     */
    public Map<String, PropertyConstraintDescription> describe(Class<?> entityClass) {
        BeanConstraintDescription description = descriptor.describeBean(entityClass);
        return describe(description);
    }

    private Map<String, PropertyConstraintDescription> describe(BeanConstraintDescription description) {
        return new HashMap<>(description.getProperties());
    }

    /**
     * Registers a class.
     * @param beanType the bean type
     */
    public void registerClass(Class<?> beanType) {
        beanTypes.add(beanType);
    }

    /**
     * Register all entity classes known in our factory.
     * @param factory the entity factory
     */
    public void registerClasses(EntityFactory factory) {
        Set<Class<?>> entityClasses = factory.getEntityClasses();
        entityClasses.forEach(this::registerClass);
    }

}