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
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

/**
 * AttributeMetadataAnalyzer is a class which can analyze a passed attribute.
 * Analyses the passed attribute's PersistenceAttributeType and the attribute's cascade annotations.
 * The former is needed to determine if an entity holds a reference and the latter to determine if this needs to be cascaded manualy.
 * is used to check if a referential annotation holds the necessary cascadeTypes.
 * 
 * @author Sander Benschop
 *
 */
public final class AttributeMetadataAnalyzer {

    /**
     * Returns true if attribute has a PersistentAttributeType of either MANY_TO_ONE or MANY_TO_MANY.
     * The function attribute.IsAssociation() does not suffice as this only returns true for MANY_TO_MANY associations.
     * @param attribute Attribute to check the PersistenceAttributeType of.
     * @return True if type is MANY_TO_ONE or MANY_TO_MANY
     */
    public static boolean attributeIsAssociation(Attribute<?,?> attribute){
    	PersistentAttributeType attributePersistenceType = attribute.getPersistentAttributeType();
    	return (attributePersistenceType == PersistentAttributeType.MANY_TO_ONE) || (attributePersistenceType == PersistentAttributeType.MANY_TO_MANY);
    }
	
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

    /**
     * Collects cascadetypes annotations from attribute.
     * @param attribute Attribute to search for cascade annotations in.
     * @return Array of found CascadeTypes
     */
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
    
    /**
     * Checks if CascadeType.MERGE and CascadeType.PERSIST annotations are present.
     * Alternatively, CascadeType.ALL may also be present.
     * @param cascades Array of cascadeTypes passed from collectCascadeTypes function.
     * @return True if MERGE && PERSIST or ALL are/is present.
     */
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
