package org.jarbframework.populator.excel.entity.persist;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * CascadeAnnotationChecker is used to check if a referential annotation holds the necessary cascadeTypes.
 * We need both CascadeType.PERSIST and CascadeTYPE.MERGE to be active, otherwise the foreign key constraints will fail.
 * Instead of these two, CascadeType.ALL may also be active.
 * @author Sander Benschop
 *
 */
public final class CascadeAnnotationChecker {

    /** Utility class, do not instantiate. */
    private CascadeAnnotationChecker() {
    }

    /**
     * Checks if an annotation holds cascade parameters. At this time only ManyToMany and ManyToOne are supported.
     * We are looking for both the PERSIST and MERGE annotation (or ALL). 
     * If these are not both available, the component will make load the referenced objects itself.
     * This is done because we cannot use the client application's logic or DAO to correct the order of persistence.
     * @param annotation Annotation to read the cascadeTypes from.
     * @return True if both the CascadeType.MERGE and CascadeType.PERSIST (or CascadeType.ALL which include these two) are found on the annotation.
     */
    public static boolean hasNecessaryCascadeAnnotations(Annotation annotation) {
        if (annotation.annotationType() == javax.persistence.ManyToOne.class) {
            ManyToOne manyToOne = (ManyToOne) annotation;
            return checkForMergeAndPersistCascadeType(manyToOne.cascade());
        } else if (annotation.annotationType() == javax.persistence.ManyToMany.class) {
            ManyToMany manyToMany = (ManyToMany) annotation;
            return checkForMergeAndPersistCascadeType(manyToMany.cascade());
        }

        return false;

    }

    /**
     * Retrieves an array of CascadeTypes and casts this to an ArrayList so it can be compared.
     * If CascadeType.ALL is present or if both CascadeType.MERGE and CascadeType.PERSIST are present, it will return true
     * @param cascades Array of CascadeTypes
     * @return True if CascadeType.ALL is present or if both CascadeType.MERGE and CascadeType.PERSIST are present
     */
    private static boolean checkForMergeAndPersistCascadeType(CascadeType[] cascades) {
        List<CascadeType> cascadeTypeList = Arrays.asList(cascades);

        if (cascadeTypeList.contains(CascadeType.ALL)) {
            return true;
        }

        List<CascadeType> needed = getListOfNeededCascadeTypes();
        return cascadeTypeList.containsAll(needed);
    }

    /**
     * Returns a list of all needed CascadeTypes.
     * @return ArrayList with CascadeType.MERGE and CascadeType.PERSIST
     */
    private static List<CascadeType> getListOfNeededCascadeTypes() {
        List<CascadeType> needed = new ArrayList<CascadeType>();
        needed.add(CascadeType.MERGE);
        needed.add(CascadeType.PERSIST);
        return needed;
    }
}
