package org.jarbframework.populator.excel.entity.persist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.metamodel.Attribute;

/**
 * CascadeAnnotationChecker is used to check if a referential annotation holds the necessary cascadeTypes.
 * We need both CascadeType.PERSIST and CascadeTYPE.MERGE to be active, otherwise the foreign key constraints will fail.
 * Instead of these two, CascadeType.ALL may also be active.
 * @author Sander Benschop
 *
 */
public final class CascadeAnnotationChecker {

    /**
     * Checks if an annotation holds cascade parameters. At this time only ManyToMany and ManyToOne are supported.
     * We are looking for both the PERSIST and MERGE annotation (or ALL). 
     * If these are not both available, the component will make load the referenced objects itself.
     * This is done because we cannot use the client application's logic or DAO to correct the order of persistence.
     * @param annotation Annotation to read the cascadeTypes from.
     * @return True if both the CascadeType.MERGE and CascadeType.PERSIST (or CascadeType.ALL which include these two) are found on the annotation.
     */
    public static boolean hasNecessaryCascadeAnnotations(Attribute<?, ?> attribute) {
        return checkForMergeAndPersistCascadeType(collectCascadeTypes(attribute));
    }

    private static CascadeType[] collectCascadeTypes(Attribute<?, ?> attribute) {
        Field field = (Field) attribute.getJavaMember();
        switch (attribute.getPersistentAttributeType()) {
        case MANY_TO_MANY:
            return field.getAnnotation(ManyToMany.class).cascade();
        case MANY_TO_ONE:
            return field.getAnnotation(ManyToOne.class).cascade();
        case ONE_TO_MANY:
            return field.getAnnotation(OneToMany.class).cascade();
        case ONE_TO_ONE:
            return field.getAnnotation(OneToOne.class).cascade();
        default:
            return new CascadeType[0];
        }
    }

    private static boolean checkForMergeAndPersistCascadeType(CascadeType[] cascades) {
        List<CascadeType> cascadeTypeList = Arrays.asList(cascades);
        if (cascadeTypeList.contains(CascadeType.ALL)) {
            return true;
        } else {
            List<CascadeType> expectedCascadeTypes = new ArrayList<CascadeType>();
            expectedCascadeTypes.add(CascadeType.MERGE);
            expectedCascadeTypes.add(CascadeType.PERSIST);
            return cascadeTypeList.containsAll(expectedCascadeTypes);
        }
    }

}
