package org.jarb.populator.excel.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two instantations of objects by name.
 * This is used to sort them alphabetically.
 * @author Sander Benschop
 *
 */
public final class ObjectClassInstantationComparator implements Comparator<Object>, Serializable {

    /** Serial id. */
    private static final long serialVersionUID = -7916225264658517785L;

    /**
     * Compares two classes by name.
     * @param class1 The first class necessary for comparison
     * @param class2 The other necessary class
     * @return Negative integer if class2 > class1, positive integer if class1 > class2 and 0 if equal
     */
    @Override
    public int compare(Object class1, Object class2) {
        return class1.getClass().getName().compareTo(class2.getClass().getName());
    }

}
