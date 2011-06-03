package org.jarb.populator.excel.metamodel.generator;

import javax.persistence.metamodel.EntityType;

/**
 * Holds functions to check for relationship, used by both SubclassRetriever and SuperclassRetriever.
 * @author Sander Benschop
 *
 */
public final class RelationshipCheckFunctions {

    /** Private constructor. */
    private RelationshipCheckFunctions() {
    }

    /**
     * Checks if subEntity is a subclass of superEntity.
     * @param subEntity Entity from JPA's metamodel
     * @param superEntity Entity from JPA's metamodel
     * @return True if subEntity is subclass of superEntity
     */
    protected static boolean isSubClassOf(EntityType<?> subEntity, EntityType<?> superEntity) {
        boolean returnValue = false;
        if (subEntity.getSupertype() == null) {
            returnValue = false;
        } else if (subEntity.getSupertype().getJavaType() == superEntity.getJavaType()) {
            returnValue = true;
        } else if (isSubClassOfMappedSuperClass(subEntity, superEntity)) {
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Checks if entity is supclass of a @MappedSuperclass.
     * @param subEntity Subentity
     * @param superEntity Possible super entity
     * @return true if super entity is subentity's superclass
     */
    private static boolean isSubClassOfMappedSuperClass(EntityType<?> subEntity, EntityType<?> superEntity) {
        boolean returnValue = false;
        if (subEntity.getSupertype().getSupertype() == null) {
            returnValue = false;
        } else if (subEntity.getSupertype().getSupertype().getJavaType() == superEntity.getJavaType()) {
            returnValue = true;
        }
        return returnValue;
    }
}
