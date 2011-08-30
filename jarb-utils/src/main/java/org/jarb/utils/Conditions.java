/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.hamcrest.Matcher;

/**
 * Used to perform assertions in code.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public class Conditions {

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static <T> T instanceOf(Object object, Class<? extends T> type, String message) {
        state(notNull(type, "Instance-of type cannot be null").isInstance(object), message);
        return (T) object;
    }

    public static <T> T notNull(T object, String message) {
        state(object != null, message);
        return object;
    }

    public static String hasText(String object, String message) {
        state(isNotBlank(object), message);
        return object;
    }

    public static <T> T matches(T object, Matcher<? super T> matcher, String message) {
        state(notNull(matcher, "Matcher cannot be null").matches(object), message);
        return object;
    }

}
