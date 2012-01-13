package org.jarbframework.utils.orm.jpa;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;

import org.jarbframework.utils.orm.NotAnEntityException;
import org.jarbframework.utils.orm.SchemaMapper;

/**
 * JPA meta model support functionality.
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public final class JpaMetaModelUtils {

    /**
     * Determine if a bean class is annotated with @Entity.
     * @param beanClass class of the bean
     * @return {@code true} if it is annotated, else {@code false}
     */
    public static boolean isEntity(Class<?> beanClass) {
        return beanClass.getAnnotation(Entity.class) != null;
    }

    /**
     * Determine if a bean class is annotated with @Embeddable.
     * @param beanClass class of the bean
     * @return {@code true} if it is annotated, else {@code false}
     */
    public static boolean isEmbeddable(Class<?> beanClass) {
        return beanClass.getAnnotation(Embeddable.class) != null;
    }

    /**
     * Retrieve all "root" entities described in our JPA meta-model.
     * @param metamodel JPA meta-model, containing all entity descriptions
     * @return entity types for each root entity class
     */
    public static Collection<EntityType<?>> getRootEntities(Metamodel metamodel) {
        Set<EntityType<?>> entityTypes = new HashSet<EntityType<?>>();
        for (EntityType<?> entityType : metamodel.getEntities()) {
            if (!hasEntitySuperclass(entityType.getJavaType())) {
                entityTypes.add(entityType);
            }
        }
        return entityTypes;
    }
    
    /**
     * Returns the field from the enclosing class which is a collection of beanClass type.
     * @param beanClass BeanClass to search the enclosingClass for
     * @param enclosingClass Enclosing class to search the beanClass in
     * @return Collection field.
     */
    public static String getFieldNameForElementCollectionClass(Class<?> beanClass, Class<?> enclosingClass, EntityManagerFactory entityManagerFactory) {
    	String tableName = "";
        Field field = getFieldThatsOfClassType(beanClass, enclosingClass.getDeclaredFields());
        if (field != null){
    	    tableName = createTableNameForElementCollection(field, enclosingClass, entityManagerFactory);
    	}
    	return tableName;
    }
    
    /**
     * Returns a field thats of the type of the passed Class from an array of fields.
     * @param clazz Class type to compare against
     * @param fieldArray FieldArray to loop through
     * @return Field of passed class type.
     */
    private static Field getFieldThatsOfClassType(Class<?> clazz, Field[] fieldArray) {
    	for (Field field : fieldArray) {
			if (fieldIsOfClassType(clazz, field)) {
				return field;
			}
    	}
    	return null;
    }

    /**
     * Returns true if the passed field is a collection of the passed clazz type.
     * @param clazz Class type to compare against
     * @param field Field type to compare to
     * @return True if types are equal
     */
	public static boolean fieldIsOfClassType(Class<?> clazz, Field field) {
		boolean isOfClassType = false;
		if ((field.getGenericType() instanceof ParameterizedType) && (field.getAnnotation(ElementCollection.class) != null)) {
			List<Type> types = Arrays.asList(((ParameterizedType) field.getGenericType()).getActualTypeArguments());
			if (types.get(0) == clazz) {
				isOfClassType = true;
			}
		}
		return isOfClassType;
	}

    /**
     * Deduces a table name for the collection from either the @CollectionTable name or the simple name of the class.
     * @param field Field to check the annotations for
     * @param beanClass Beanclass to get the simple name from
     * @return Table name
     */
	public static String createTableNameForElementCollection(Field field, Class<?> enclosingClass, EntityManagerFactory entityManagerFactory) {
		String tableName = "";
		if (field.getAnnotation(CollectionTable.class) != null) {
			CollectionTable collectionTable = field.getAnnotation(CollectionTable.class);
			tableName = collectionTable.name();
		} else {
			tableName = createElementCollectionTableNameByJPADefault(enclosingClass, field.getName(), entityManagerFactory);
		}
		return tableName;
	}
	
	/**
	 * Creates a table name for an Element Collection table by concatinating the name of the enclosed entity and the fieldName of the Collection.
	 * As specified in the JPA spec
	 * @param enclosingClass The class the collection field is situated in
	 * @param fieldName Fieldname of collection field
	 * @param entityManagerFactory EntityManagerFactory to get schemamapper from.
	 * @return Table name
	 */
	private static String createElementCollectionTableNameByJPADefault(
			Class<?> enclosingClass, String fieldName, EntityManagerFactory entityManagerFactory) {
		SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(entityManagerFactory);
		String enclosingTypeTableName = schemaMapper.tableNameOf(enclosingClass);
		return enclosingTypeTableName + "_" + fieldName;
	}
	
	/**
	 * Deduces an identifier column name by concatinating the table name of the enclosing type with the identifier column name.
	 * As specified in the JPA spec.
	 * @param enclosingClass Enclosing class to get the information from
	 * @param entityManagerFactory EntityManagerFactory to get the schemaMapper from
	 * @return Identifier column name
	 */
	@Deprecated
	public static String deduceIdentifierColumnName(Class<?> enclosingClass, EntityManagerFactory entityManagerFactory) {
		SchemaMapper schemaMapper = JpaHibernateSchemaMapper.usingNamingStrategyOf(entityManagerFactory);
		String enclosingTypeTableName = schemaMapper.tableNameOf(enclosingClass);
		String idColumnName = JpaMetaModelUtils.getIdentifierField(enclosingClass).getAnnotation(Column.class).name();
		return enclosingTypeTableName + "_" + idColumnName;
	}

	/**
	 * Returns the field from a defined Class that's annotated by @Id
	 * @param definedClass Defined class to search in
	 * @return Field with @Id annotation
	 */
	public static Field getIdentifierField(Class<?> definedClass) {
        if (!isEntity(definedClass)) {
        	String message = String.format("Class '%s' is not an entity and thus has no identifier field.", definedClass.getName());
        	throw new NotAnEntityException(message);
        }
		
		for (Field field : definedClass.getDeclaredFields()) {
        	if (field.getAnnotation(Id.class) != null) {
        		return field;
        	}
        }
        return null;
	}

    /**
     * Returns all elementCollections that are present inside an entity.
     * @param entity Entity to analyse
     * @return Collection of EmbeddableType<?>
     */
    public static Collection<EmbeddableType<?>> getElementCollectionsForEntity(EntityType<?> entity) {
    	Set<EmbeddableType<?>> elementCollections = new HashSet<EmbeddableType<?>>();
    	for (PluralAttribute<?, ?, ?> pluralAttribute : entity.getPluralAttributes()) {
    		if (pluralAttribute.getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION){
    			EmbeddableType<?> elementCollection = (EmbeddableType<?>) pluralAttribute.getElementType();
    			elementCollections.add(elementCollection);
    		}
    	}
    	return elementCollections;
    }
    
    /**
     * Returns true if passed class has a superclass that is an @Entity.
     * @param beanClass Bean class to analyze
     * @return True if superclass is @Entity
     */
    private static boolean hasEntitySuperclass(Class<?> beanClass) {
        boolean entitySuperClassFound = false;
        Class<?> currentClass = beanClass;
        while ((currentClass = currentClass.getSuperclass()) != null) {
            if (isEntity(currentClass)) {
                entitySuperClassFound = true;
                break;
            }
        }
        return entitySuperClassFound;
    }

    /**
     * Retrieve the root entity class of a bean.
     * @param beanClass class of the bean
     * @return root entity class, if any could be found
     */
    public static Class<?> findRootEntityClass(Class<?> beanClass) {
        Class<?> entityClass = null;
        Class<?> currentClass = beanClass;
        do {
            if (isEntity(currentClass)) {
                entityClass = currentClass;
            }
        } while ((currentClass = currentClass.getSuperclass()) != null);
        return entityClass;
    }

    private JpaMetaModelUtils() {
    }

}
