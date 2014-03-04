/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.orm;

public class MappingUtils {
    
    /**
     * Convert a camel cased name into lower cases, separating
     * each previous upper case with a '_'. For example:
     * 
     * <p>
     * 
     * <code>
     *  MySillyName => my_silly_name<br>
     *  My1Name => my_1_name<br>
     *  MyName => my_name<br>
     *  Hello => hello
     * </code>
     * 
     * @param name the name to lower case
     * @return the lower cased name
     */
    public static String lowerCaseWithUnderscores(String name) {
        StringBuilder buffer = new StringBuilder(name.replace('.', '_'));
        for (int index = 1; index < buffer.length() - 1; index++) {
            if (isSeparator(buffer, index)) {
                buffer.insert(index++, '_');
            }
        }
        return buffer.toString().toLowerCase();
    }
    
    private static boolean isSeparator(StringBuilder buffer, int index) {
        char previous = buffer.charAt(index - 1);
        char current = buffer.charAt(index);
        char next = buffer.charAt(index + 1);
        
        return (Character.isLowerCase(previous) || Character.isDigit(previous)) 
                && Character.isUpperCase(current)
                && (Character.isLowerCase(next) || Character.isDigit(next));
    }

}
