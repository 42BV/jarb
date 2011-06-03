package org.jarb.populator.excel.workbook.validator;

import java.lang.reflect.Field;
import java.util.Set;

import org.jarb.populator.excel.metamodel.generator.SuperclassRetriever;

/**
 * Checks regular fields for existence.
 * @author Sander Benschop
 *
 */
public final class FieldValidator {

    /** Private constructor. */
    private FieldValidator() {
    }

    /**
     * Checks if the columnDefinition's field is a valid field.
     * If field is not from the persistent class the Excelrow was created from the superclasses will be checked.
     * @param fieldname Name of the field that is being sought
     * @param persistentClass Persistent class 
     * @param superClasses Set of superclasses needed to search through
     * @return True or false, depending on its validity.
     * @throws NoSuchFieldException Throws if field is not found
     * 
     */
    public static boolean isExistingField(String fieldname, Class<?> persistentClass) {
        if (fieldname == null) {
            return false;
        } else if (FieldRetriever.tryToGetFieldFromClass(fieldname, persistentClass) != null) {
            return true;
        }
        Set<Class<?>> superClasses = SuperclassRetriever.getListOfSuperClasses(persistentClass);
        return isFieldInASuperclass(fieldname, superClasses);
    }

    /**
    * Loops over the set of superClasses to find the field.
     * @param fieldname Name of the field that is being sought
     * @param superClasses Set of superclasses to search through
     * @return True if field is in a superclass, otherwise false
     */
    private static boolean isFieldInASuperclass(String fieldname, Set<Class<?>> superClasses) {
        if (superClasses == null) {
            return false;
        }
        for (Class<?> superClass : superClasses) {
            Field field = FieldRetriever.tryToGetFieldFromClass(fieldname, superClass);
            if (field != null) {
                return true;
            }
        }
        return false;
    }
}
