/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils;

/**
 * Utility for handling {@link String} objects. We created this class
 * rather than using the Apache Commons to keep the classpath as small
 * as possible.
 *
 * @author Jeroen van Schagen
 * @since Mar 5, 2014
 */
public class StringUtils {
    
    private static final String EMPTY = "";
    private static final int INDEX_NOT_FOUND = -1;

    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }
    
    public static String substringBefore(String text, String separator) {
        int index = indexOf(text, separator);
        return substringBefore(text, index);
    }
    
    public static String substringBeforeLast(String text, String separator) {
        int index = lastIndexOf(text, separator);
        return substringBefore(text, index);
    }
    
    private static String substringBefore(String text, int index) {
        if (index == INDEX_NOT_FOUND) {
            return text;
        }
        return text.substring(0, index);
    }

    public static String substringAfter(String text, String separator) {
        int index = indexOf(text, separator);
        return substringAfter(text, index);
    }
    
    public static String substringAfterLast(String text, String separator) {
        int index = lastIndexOf(text, separator);
        return substringAfter(text, index);
    }
    
    private static String substringAfter(String text, int index) {
        if (index == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return text.substring(index + 1, text.length());
    }
    
    private static int indexOf(String text, String separator) {
        if (isBlank(text)) {
            return INDEX_NOT_FOUND;
        }
        return text.indexOf(separator);
    }
    
    private static int lastIndexOf(String text, String separator) {
        if (isBlank(text)) {
            return INDEX_NOT_FOUND;
        }
        return text.lastIndexOf(separator);
    }

    public static boolean startsWithIgnoreCase(String text, String value) {
        return text.toLowerCase().startsWith(value.toLowerCase());
    }
    
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
