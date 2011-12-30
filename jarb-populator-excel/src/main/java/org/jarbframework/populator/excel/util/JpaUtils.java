package org.jarbframework.populator.excel.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.proxy.HibernateProxy;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.ElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;

/**
 * Java Persistence API (JPA) utilities.
 * 
 * @author Jeroen van Schagen
 * @since 11-05-2011
 */
public final class JpaUtils {

    /** Utility class, do not attempt to instantiate. */
    private JpaUtils() {
    }
    
    /**
     * Create a new {@link EntityManager}, using all configured properties.
     * @param entityManagerFactory factory that builds entity managers
     * @return new entity manager instance
     */
    public static EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager(entityManagerFactory.getProperties());
    }

    /**
     * Retrieve the identifier (@Id) value of an entity.
     * @param entity reference to the entity
     * @param entityManagerFactory entity manager factory
     * @return identifier of the entity, if any
     */
    public static Object getIdentifier(Object entity, EntityManagerFactory entityManagerFactory) {
        if (entity instanceof HibernateProxy) {
            entity = ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entityManagerFactory.getPersistenceUnitUtil().getIdentifier(entity);
    }

    /**
     * Returns the defined class of passed definition if it possesses one.
     * @param definition Definition to get defined class from
     * @return Defined class
     */
    //TO-DO Remove this function and replace by proper Definition type structure.
    public static Class<?> getDefinedClassOfDefinition(Definition definition){
        Class<?> definedClass = null;
        if (definition instanceof EntityDefinition<?>){
            definedClass = ((EntityDefinition<?>) definition).getDefinedClass();
        } else if (definition instanceof ElementCollectionDefinition<?>){
            definedClass = ((ElementCollectionDefinition<?>) definition).getDefinedClass();
        }
        return definedClass;
    }
    
    /**
     * Returns the table name of passed definition if it possesses one.
     * @param definition Definition to get table name from
     * @return Table name
     */
    //TO-DO Remove this function and replace by proper Definition type structure.
    public static String getTableNameOfDefinition(Definition definition){
        String tableName = null;
        if (definition instanceof EntityDefinition<?>){
            tableName = ((EntityDefinition<?>) definition).getTableName();
        } else if (definition instanceof ElementCollectionDefinition<?>){
            tableName = ((ElementCollectionDefinition<?>) definition).getTableName();
        }
        return tableName;
    }
    
}
