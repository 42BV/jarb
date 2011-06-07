package org.jarb.populator.excel.workbook.validator;

import java.lang.reflect.Field;

/**
 * Class to retrieve fields from a perisistent class.
 * @author Sander Benschop
 *
 */
public final class FieldRetriever {

    /** Private constructor. */
    private FieldRetriever() {
    }

    /**
     * Tries to get a field from a class, if it's available.
     * @param fieldname Field name to search for, can be embedded or regular.
     * @param classToCheck Class to get the field from.
     * @return Field is available in persistent class, otherwise null.
     */
    protected static Field tryToGetFieldFromClass(String fieldname, Class<?> classToCheck) {
        Field field = null;
        try {
            field = classToCheck.getDeclaredField(fieldname);
        } catch (NoSuchFieldException e) {
        }
        return field;
    }
}
