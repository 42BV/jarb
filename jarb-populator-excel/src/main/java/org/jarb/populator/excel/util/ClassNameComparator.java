package org.jarb.populator.excel.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two classes by name, this is used to sort lists of classes alphabetically.
 * Overrides the Comparator class
 * @author Sander Benschop
 *
 */
public final class ClassNameComparator implements Comparator<Class<?>>, Serializable {

    private static final long serialVersionUID = -6570810191082085755L;

    /**
     * Compares two classes by name.
     * @param left The first class necessary for comparison
     * @param right The other necessary class
     * @return Negative integer if class2 > class1, positive integer if class1 > class2 and 0 if equal
     */
    @Override
    public int compare(Class<?> left, Class<?> right) {
        return left.getName().compareTo(right.getName());
    }

}
