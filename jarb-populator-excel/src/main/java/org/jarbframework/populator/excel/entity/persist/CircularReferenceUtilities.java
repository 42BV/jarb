package org.jarbframework.populator.excel.entity.persist;

import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.metamodel.generator.SuperclassRetriever;
import org.jarbframework.utils.bean.ModifiableBean;

public final class CircularReferenceUtilities {
    
    /**
     * Returns true if the referencedObject has been handled in this iteration before, thus being an infinite loop.
     * @param referencedEntity Object to check the list for.
     * @return True if cascading has looped.
     */
    public static boolean cascadingHasLooped(Object referencedEntity, Set<Object> cascadedObjects) {
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
     * Creates a temporaryObject filled with a nullable reference to another object within a referential circle.
     * This is needed because the field in the regular object will be made null to resolve this problem.
     * Afterwards we still want to persist this data, that's why it's kept in a temporaryObject.
     * If the referencedObject is not nullable, this function will return null.
     * @param entity Entity to compare the refName to.
     * @param metamodel JPA's metamodel
     * @param refName Name of referenced attribute
     * @return Temporary object or null.
     */
    public static Object createTemporaryObjectForCircularReference(Object entity, Metamodel metamodel, String refName) {
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
    public static String getReferentialFieldname(Object entity, Object referencedEntity, Metamodel metamodel) {
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
