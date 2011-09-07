package org.jarb.populator.excel.entity.persist;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.jarb.populator.excel.metamodel.generator.SuperclassRetriever;
import org.jarb.populator.excel.util.JpaUtils;
import org.jarbframework.utils.bean.ModifiableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ReferentialPreparement class can prepare classes for persistence by persisting needed referenced objects first.
 * Only does this if they're NOT annotated with cascadeTypes both PERSIST and MERGE or ALL.
 * If it is annotated as one of those, it will still check if the referenced item holds a reference that isn't.
 * @author Sander Benschop
 *
 */
public final class ReferentialPreparement {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferentialPreparement.class);

    /** Static class, do not instantiate. */
    private ReferentialPreparement() {
    }

    /**
     * Prepares an entity's foreign references. It gets the entity's class from the metamodel and inspects its attributes.
     * If an attribute is found to have an attributePersistentType of either MANY_TO_ONE or MANY_TO_MANY the attribute's field will be loaded.
     * The annotations from this field are being read (if the cascade types are lacking they will be persisted first and added to the databaseRecord)
     * @param entity JPA Metamodel Entity
     * @param entityManager Interface used to interact with the persistence context
     * @param jpaDao Jpa data access object by Hactar
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     * @return Object with prepared entity references (either persisted or not, depending on lack of cascade types).
     */
    public static Object prepareEntityReferences(Object entity, EntityManager entityManager, Set<Object> cascadedObjectsInThisInteration) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        Metamodel metamodel = entityManagerFactory.getMetamodel();

        for (Attribute<?, ?> attribute : metamodel.entity(entity.getClass()).getAttributes()) {
            PersistentAttributeType attributePersistenceType = attribute.getPersistentAttributeType();
            if ((attributePersistenceType == PersistentAttributeType.MANY_TO_ONE) || (attributePersistenceType == PersistentAttributeType.MANY_TO_MANY)) {
                prepareAttribute(entity, entityManager, attribute, cascadedObjectsInThisInteration);
            }
        }
        return entity;
    }

    /**
     * Loops over all annotations in the passed attribute. Then checks if these have the proper CascadeType annotations.
     * If so, we'll have to check if these attributes also have references that might need to be persisted first.
     * If not, we will have to check that one too.
     * @param entity JPA Metamodel Entity
     * @param jpaDao Jpa data access object by Hactar
     * @param entityManager EntityManager needed to search through the persistence cache.
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     * @param attribute Attribute from the Entity   
     */
    private static void prepareAttribute(Object entity, EntityManager entityManager, Attribute<?, ?> attribute, Set<Object> cascadedObjectsInThisInteration) {
        Field field = (Field) attribute.getJavaMember();
        for (Annotation annotation : field.getAnnotations()) {
            if (!CascadeAnnotationChecker.hasNecessaryCascadeAnnotations(annotation)) {
                retrieveReferencesForAttribute(entity, entityManager, attribute, cascadedObjectsInThisInteration);
            } else {
                Object referencedObject = ModifiableBean.wrap(entity).getPropertyValue(attribute.getName());
                if (!cascadedObjectsInThisInteration.contains(referencedObject)) {
                    cascadedObjectsInThisInteration.add(referencedObject);
                    secondaryReferenceCheck(referencedObject, entityManager, cascadedObjectsInThisInteration);
                }
            }
            break;
        }
    }

    /**
     * Checks if referenced objects in classes that DO have Cascade annotations also have cascade annotations.
     * Otherwise it will still generate exceptions.
     * @param referencedObject Referenced object to check.
     * @param entityManagerFactory EntityManagerFactory needed to create the entityManager and PersistentUnitUtil from
     * @param jpaDao Jpa data access object by Hactar
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     */
    private static void secondaryReferenceCheck(Object referencedObject, EntityManager entityManager, Set<Object> cascadedObjectsInThisInteration) {
        if (referencedObject != null) {
            if (referencedObject instanceof Collection<?>) {
                prepareEntitiesFromSet(entityManager, referencedObject, cascadedObjectsInThisInteration);
            } else {
                prepareEntityReferences(referencedObject, entityManager, cascadedObjectsInThisInteration);
            }
        }
    }

    /**
     * Checks if an entity's attribute is a Collection or not. Then takes the appropriate action to retrieve the referenced object.
     * @param entity JPA Metamodel Entity
     * @param jpaDao Jpa data access object by Hactar
     * @param entityManager EntityManager needed to search through the persistence cache.
     * @param attribute Attribute from the Entity
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     */
    private static void retrieveReferencesForAttribute(Object entity, EntityManager entityManager, Attribute<?, ?> attribute,
            Set<Object> cascadedObjectsInThisInteration) {
        Object referencedObject = ModifiableBean.wrap(entity).getPropertyValue(attribute.getName());
        if (referencedObject instanceof Collection<?>) {
            prepareEntitiesFromSet(entityManager, referencedObject, cascadedObjectsInThisInteration);
        } else {
            setFieldValuesForReferencedObject(entity, entityManager, attribute.getName(), referencedObject, cascadedObjectsInThisInteration);
        }
    }

    /**
     * Prepares a set of entities for persistence.
     * Saves them afterwards so the referencing entity will know where to point to.
     * @param jpaDao Jpa data access object by Hactar.
     * @param entityManager EntityManager needed to search through the persistence cache.
     * @param referencedObject Refenced object which is a collection item.
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     */
    private static void prepareEntitiesFromSet(EntityManager entityManager, Object referencedObject, Set<Object> cascadedObjectsInThisInteration) {
        @SuppressWarnings("unchecked")
        HashSet<Object> referencedObjectSet = (HashSet<Object>) referencedObject;
        for (Object referencedObjectFromSet : referencedObjectSet) {
            LOGGER.info("Cascading Excelrow of class: {}", referencedObjectFromSet.getClass());
            entityManager.merge(prepareEntityReferences(referencedObjectFromSet, entityManager, cascadedObjectsInThisInteration));
        }
    }

    /**
     * Sets the field values of the referenced object.
     * Checks if PersistenceUnitUtil can retrieve an ID for the referenced object.
     * If it can, it means that it's already been persisted and is added as a new value for referencedObject.
     * If it cannot be found, it will be persisted first and then added.
     * For each object it's checked if it's inside the 'cascadedObjectsinThisIteration' set.
     * This is a set that's filled with all the processed objects in an iteration and then emptied.
     * So if an object is already in that set, we know it's a circular reference and must take action. Otherwise it'll loop eternally.
     * @param entity JPA Metamodel Entity
     * @param entityManager EntityManager needed to search through the persistence cache.
     * @param attributeName Name of the attribute tha's being edited.
     * @param referencedObject The referenced object
     * @param jpaDao Jpa data access object by Hactar.
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     * @return Entity with persisted references
     */
    private static Object setFieldValuesForReferencedObject(Object entity, EntityManager entityManager, String attributeName, Object referencedObject,
            Set<Object> cascadedObjectsInThisInteration) {
        if (referencedObject != null) {
            Object identifier = JpaUtils.getIdentifier(referencedObject, entityManager.getEntityManagerFactory());
            if (identifier != null) {
                Object retrievenObject = entityManager.find(referencedObject.getClass(), identifier);
                if (retrievenObject != null) {
                    // Entity has already been persisted, couple to persisted value
                    ModifiableBean.wrap(entity).setPropertyValue(attributeName, retrievenObject);
                } else {
                    // Entity claimed to have an identifier, but is not known in database
                    cascadeReferencedObject(entity, entityManager, attributeName, referencedObject, cascadedObjectsInThisInteration);
                }
            } else {
                // Entity has no identifier yet
                cascadeReferencedObject(entity, entityManager, attributeName, referencedObject, cascadedObjectsInThisInteration);
            }
        }
        return entity;
    }

    /**
     * Handles a referenced object that has been proven not to be persistent.
     * First it's checked if it's inside the cascadedObjectInThisIteration set. 
     * If it is, it points to an circular referency and special action must be taken.
     * If it's not, the referenced object will recursively be checked for referenced objects as well
     * and afterwards it's saved and re-added to the entity.
     * @param entity JPA Metamodel Entity
     * @param entityManager EntityManager needed to search through the persistence cache.
     * @param attributeName Name of the attribute tha's being edited.
     * @param referencedObject The referenced object
     * @param jpaDao Jpa data access object by Hactar.
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     */
    private static void cascadeReferencedObject(Object entity, EntityManager entityManager, String attributeName, Object referencedObject,
            Set<Object> cascadedObjectsInThisInteration) {
        //ReferencedObject must be checked as well...

        if (!cascadingHasLooped(referencedObject, cascadedObjectsInThisInteration)) {
            referencedObject = prepareEntityReferences(referencedObject, entityManager, cascadedObjectsInThisInteration);
            LOGGER.info("Cascading Excelrow of class: " + referencedObject.getClass());
            ModifiableBean.wrap(entity).setPropertyValue(attributeName, entityManager.merge(referencedObject));
        } else {
            resolveCircularReferencing(entity, entityManager, referencedObject, cascadedObjectsInThisInteration);
        }
    }

    /**
     * Returns true if the referencedObject has been handled in this iteration before, thus being an infinite loop.
     * @param referencedObject Object to check the list for.
     * @param cascadedObjectsInThisInteration List with objects that have previously been handled.
     * @return True or false.
     */
    private static boolean cascadingHasLooped(Object referencedObject, Set<Object> cascadedObjectsInThisInteration) {
        if (cascadedObjectsInThisInteration.contains(referencedObject)) {
            return true;
        }

        for (Object object : cascadedObjectsInThisInteration) {
            if (referencedObject.getClass() == object.getClass()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Resolves a circular referency. The entity passed to this function is known to be in a circular loop.
     * This problem can be caused by two objects holding references to each other, the self-created cascading functions will call each other infinitely.
     * This is resolved by removing the referenced object on the nullable side. 
     * There must be one, because otherwise it's downright impossible to ever insert data.
     * The referenced object is stored in a temporary new object called temporaryObject. 
     * The other data from the referenced object is being saved after checking if it has other uncascaded references.
     * Then, the temporaryObject is checked itself and then put back inside the referencedObject.
     * <br><br>
     * Note: circular references in the domain entities, though supported by this component, 
     * might not be the best form of implementation and should be avoided in most situations.
     * @param entity Entity that's in a circular referency.
     * @param entityManager EntityManager needed to check the persistence cache.
     * @param referencedObject The referenced object that points to another object which in turn points back to entity.
     * @param jpaDao Jpa data access object by Hactar.
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     */
    private static void resolveCircularReferencing(Object entity, EntityManager entityManager, Object referencedObject,
            Set<Object> cascadedObjectsInThisInteration) {

        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        String refName = getReferentialFieldname(referencedObject, entity, entityManagerFactory);
        Object temporaryObject = createTemporaryObject(entity, metamodel, refName);

        if (!cascadedObjectsInThisInteration.contains(entity)) {
            LOGGER.info("Cascading Excelrow of class: " + referencedObject.getClass());
            referencedObject = prepareEntityReferences(referencedObject, entityManager, cascadedObjectsInThisInteration);
            ModifiableBean.wrap(entity).setPropertyValue(refName, entityManager.merge(referencedObject));
            cascadedObjectsInThisInteration.add(entity);
        }
        if (temporaryObject != null) {
            temporaryObject = prepareEntityReferences(temporaryObject, entityManager, new HashSet<Object>());
            ModifiableBean.wrap(entity).setPropertyValue(refName, entityManager.merge(temporaryObject));
        }
    }

    /**
     * Creates a temporaryObject filled with a nullable reference to another object within a referential circle.
     * This is needed because the field in the regular object will be made null to resolve this problem.
     * Afterwards we still want to persist this data, that's why it's kept in a temporaryObject.
     * If the referencedObject is not nullable, this function will return null.
     * @param entity Entity to compare the refName to.
     * @param metamodel JPA's metamodel
     * @param refName Name of referenced attribute
     * @return Temporary object or null.
     */
    private static Object createTemporaryObject(Object entity, Metamodel metamodel, String refName) {
        Object temporaryObject = null;
        if (metamodel.entity(entity.getClass()).getSingularAttribute(refName).isOptional()) {
            ModifiableBean<Object> modifiableEntity = ModifiableBean.wrap(entity);
            temporaryObject = modifiableEntity.getPropertyValue(refName);
            modifiableEntity.setPropertyValue(refName, null);
        }
        return temporaryObject;
    }

    /**
     * Resolves a referential fieldname of a referencedObject from an entity.
     * @param referencedObject ReferencedObject to get the fieldname from.
     * @param entity Entity to search the fieldname in.
     * @param entityManagerFactory EntityManagerFactory needed to generate the metamodel.
     * @return Fieldname
     */
    private static String getReferentialFieldname(Object referencedObject, Object entity, EntityManagerFactory entityManagerFactory) {
        String refName = null;
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entityType = metamodel.entity(entity.getClass());
        for (Attribute<?, ?> attribute : entityType.getAttributes()) {
            if ((attribute.getJavaType() == referencedObject.getClass())
                    || SuperclassRetriever.getListOfSuperClasses(referencedObject.getClass()).contains(attribute.getJavaType())) {
                refName = attribute.getName();
                break;
            }
        }
        return refName;
    }

}
