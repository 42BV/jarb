package org.jarbframework.populator.excel.entity.persist;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.metamodel.generator.SuperclassRetriever;
import org.jarbframework.populator.excel.util.JpaUtils;
import org.jarbframework.utils.bean.ModifiableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ReferentialPreparement class can prepare classes for persistence by persisting required referenced objects first.
 * Only does this if they're NOT annotated with cascadeTypes both PERSIST and MERGE or ALL.
 * If it is annotated as one of those, it will still check if the referenced item holds a reference that isn't.
 * @author Sander Benschop
 *
 */
public class ReferentialPreparement {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferentialPreparement.class);
    private final Set<Object> cascadedObjects = new HashSet<Object>();
    private final EntityManager entityManager;

    public ReferentialPreparement(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Prepares an entity's foreign references. It gets the entity's class from the metamodel and inspects its attributes.
     * If an attribute is found to have an attributePersistentType of either MANY_TO_ONE or MANY_TO_MANY the attribute's field will be loaded.
     * The annotations from this field are being read (if the cascade types are lacking they will be persisted first and added to the databaseRecord)
     * @param entity JPA Metamodel Entity
     * @param entityManager Interface used to interact with the persistence context
     * @return Object with prepared entity references (either persisted or not, depending on lack of cascade types).
     */
    public <T> T prepareEntityReferences(T entity) {
        //Add this entity to the cascadedObjects set so we can recognize if we're in a referencing loop later on.
    	cascadedObjects.add(entity);
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        for (Attribute<?, ?> attribute : metamodel.entity(entity.getClass()).getAttributes()) {
            if (attributeIsAssociation(attribute)) {
                prepareAttribute(entity, attribute);
            }
        }
        return entity;
    }
    
    /**
     * Returns true if attribute has a PersistentAttributeType of either MANY_TO_ONE or MANY_TO_MANY.
     * The function attribute.IsAssociation() does not suffice as this only returns true for MANY_TO_MANY associations.
     * @param attribute Attribute to check the PersistenceAttributeType of.
     * @return True if type is MANY_TO_ONE or MANY_TO_MANY
     */
    private boolean attributeIsAssociation(Attribute<?,?> attribute){
    	PersistentAttributeType attributePersistenceType = attribute.getPersistentAttributeType();
    	return (attributePersistenceType == PersistentAttributeType.MANY_TO_ONE) || (attributePersistenceType == PersistentAttributeType.MANY_TO_MANY);
    }

    /**
     * Loops over all annotations in the passed attribute. Then checks if these have the proper CascadeType annotations.
     * If so, we'll have to check if these attributes also have references that might need to be persisted first.
     * If not, we will have to check that one too.
     * @param entity JPA Metamodel Entity
     * @param entityManager EntityManager needed to search through the persistence cache.
     * @param cascadedObjectsInThisInteration List with objects already cascaded.
     * @param attribute Attribute from the Entity   
     */
    private void prepareAttribute(Object entity, Attribute<?, ?> attribute) {
    	Object referencedEntity = ModifiableBean.wrap(entity).getPropertyValue(attribute.getName());
    	if (!CascadeAnnotationChecker.hasNecessaryCascadeAnnotations(attribute)) {
        	//Attribute DOESN'T hold proper Cascade annotations. We need to persist the entities referenced by this entity first.
        	//Otherwise it will generate exceptions upon persistence.
        	retrieveReferencesForEntity(entity, referencedEntity, attribute);
        } else {
        	//Attribute DOES hold the proper Cascade annotations, however we can't just persist it as its referenced objects may
        	//also contain relationships that DON'T. These need to be handled as well.
        	retrieveReferencesForReferencedEntities(entity, referencedEntity, attribute);
        }
    }

    /**
     * Persists the referenced entities of the passed entity so the entity's own persistence won't fail on account of unpersisted references.
     * Checks if an entity's attribute is a Collection or not. If it is, we need to iterate over it and persist the contents of the Collection.
     * @param entity JPA Metamodel Entity
     * @param referencedEntity Entity that is referenced by passed Entity.
     * @param attribute Attribute from the Entity
     */
    private void retrieveReferencesForEntity(Object entity, Object referencedEntity, Attribute<?, ?> attribute) {
        if (referencedEntity instanceof Iterable<?>) {
            prepareEntitiesFromSet((Iterable<?>) referencedEntity);
        } else {
            setFieldValuesForReferencedObject(entity, attribute.getName(), referencedEntity);
        }
    }    
    
    /**
     * Checks if referencedObject has references which in their turn are not annotated properly.
     * If so, these will be persisted first.
     * @param entity JPA Metamodel Entity
     * @param referencedEntity Entity that is referenced by passed Entity.
     * @param attribute Attribute from the Entity
     */
    private void retrieveReferencesForReferencedEntities(Object entity, Object referencedEntity, Attribute<?, ?> attribute){
        if (referencedEntity != null && !cascadedObjects.contains(referencedEntity)) {
            if (referencedEntity instanceof Iterable<?>) {
                prepareEntitiesFromSet((Iterable<?>) referencedEntity);
            } else {
                prepareEntityReferences(referencedEntity);
            }
        }
    }
    
    /**
     * Prepares a set of entities for persistence.
     * Saves them afterwards so the referencing entity will know where to point to.
	 * @param referencedEntitySet Set of referenced entities which need to be persisted
     */
    private void prepareEntitiesFromSet(Iterable<?> referencedEntitySet) {
        for (Object referencedEntity : referencedEntitySet) {
            LOGGER.info("Cascading Excelrow of class: {}", referencedEntity.getClass());
            entityManager.merge(prepareEntityReferences(referencedEntity));
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
     * @param attributeName Name of the attribute that's being edited.
     * @param referencedEntity The referenced entity
     * @return Entity with persisted references
     */
    private Object setFieldValuesForReferencedObject(Object entity, String attributeName, Object referencedEntity) {
        if (referencedEntity != null) {
            Object identifier = JpaUtils.getIdentifier(referencedEntity, entityManager.getEntityManagerFactory());
            if (identifier != null) {
                Object retrievenObject = entityManager.find(referencedEntity.getClass(), identifier);
                if (retrievenObject != null) {
                    // Entity has already been persisted, couple to persisted value
                    ModifiableBean.wrap(entity).setPropertyValue(attributeName, retrievenObject);
                } else {
                    // Entity claimed to have an identifier, but is not known in database
                    cascadeReferencedObject(entity, attributeName, referencedEntity);
                }
            } else {
                cascadeReferencedObject(entity, attributeName, referencedEntity);
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
     * @param attributeName Name of the attribute tha's being edited.
     * @param referencedEntity The referenced entity
     */
    private void cascadeReferencedObject(Object entity, String attributeName, Object referencedEntity) {
        if (!cascadingHasLooped(referencedEntity)) {
            referencedEntity = prepareEntityReferences(referencedEntity);
            LOGGER.info("Cascading Excelrow of class: " + referencedEntity.getClass());
            ModifiableBean.wrap(entity).setPropertyValue(attributeName, entityManager.merge(referencedEntity));
        } else {
            resolveCircularReferencing(entity, referencedEntity);
        }
    }

    /**
     * Returns true if the referencedObject has been handled in this iteration before, thus being an infinite loop.
     * @param referencedEntity Object to check the list for.
     * @return True if cascading has looped.
     */
    private boolean cascadingHasLooped(Object referencedEntity) {
        if (cascadedObjects.contains(referencedEntity)) {
            return true;
        }
        for (Object cascadedObject : cascadedObjects) {
            if (referencedEntity.getClass() == cascadedObject.getClass()) {
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
     * @param referencedEntity The referenced object that points to another object which in turn points back to entity.
     */
    private void resolveCircularReferencing(Object entity, Object referencedEntity) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        String refName = getReferentialFieldname(entity, referencedEntity, metamodel);
        Object temporaryObject = createTemporaryObject(entity, metamodel, refName);

        if (!cascadedObjects.contains(entity)) {
            LOGGER.info("Cascading Excelrow of class: " + referencedEntity.getClass());
            referencedEntity = prepareEntityReferences(referencedEntity);
            ModifiableBean.wrap(entity).setPropertyValue(refName, entityManager.merge(referencedEntity));
            cascadedObjects.add(entity);
        }
        if (temporaryObject != null) {
            temporaryObject = new ReferentialPreparement(entityManager).prepareEntityReferences(temporaryObject);
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
     * @param entity Entity to search the fieldname in.
     * @param referencedEntity ReferencedObject to get the fieldname from.
     * @param entityManagerFactory EntityManagerFactory needed to generate the metamodel.
     * @return Fieldname
     */
    private String getReferentialFieldname(Object entity, Object referencedEntity, Metamodel metamodel) {
        String refName = null;
        EntityType<?> entityType = metamodel.entity(entity.getClass());
        for (Attribute<?, ?> attribute : entityType.getAttributes()) {
            if ((attribute.getJavaType() == referencedEntity.getClass())
                    || SuperclassRetriever.getListOfSuperClasses(referencedEntity.getClass()).contains(attribute.getJavaType())) {
                refName = attribute.getName();
                break;
            }
        }
        return refName;
    }
}
