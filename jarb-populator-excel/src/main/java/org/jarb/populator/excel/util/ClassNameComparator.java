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
     * @param class1 The first class necessary for comparison
     * @param class2 The other necessary class
     * @return Negative integer if class2 > class1, positive integer if class1 > class2 and 0 if equal
     */
    @Override
    public int compare(Class<?> class1, Class<?> class2) {
        return class1.getName().compareTo(class2.getName());
    }

}
