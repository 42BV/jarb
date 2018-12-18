/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.bean;

import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

/**
 * JPA based map bean registry.
 *
 * @author Jeroen van Schagen
 * @since Apr 14, 2015
 */
public class JpaBeanRegistry extends MapBeanRegistry {
    
    /**
     * Construct a new bean registry, with all entities known in the factory.
     * 
     * @param entityManagerFactory the entity manager factory
     */
    public JpaBeanRegistry(EntityManagerFactory entityManagerFactory) {
        Set<EntityType<?>> entityTypes = entityManagerFactory.getMetamodel().getEntities();
        for (EntityType<?> entityType : entityTypes) {
            register(entityType.getBindableJavaType());
        }
    }

}
