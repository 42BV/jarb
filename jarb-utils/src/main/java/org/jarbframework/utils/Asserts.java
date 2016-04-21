package org.jarbframework.utils;


/**
 * Performs precondition checks on the code.
 * @author Jeroen van Schagen
 * @since Aug 29, 2011
 */
public final class Asserts {

    /**
     * Assert that an expression evaluates {@code true}.
     * @param expression the expression to evaluate
     * @param message the error message shown whenever our state is not satisfied
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     * @param <T> the type of object, used to type safe return the same object
     * @param object the object to check
     * @param message the error message shown whenever our state is not satisfied
     * @param <T> class type of instance
     * @return provided object, used for chaining
     */
    public static <T> T notNull(T object, String message) {
        state(object != null, message);
        return object;
    }

    /**
     * Assert that a text has content.
     * @param text the text to check
     * @param message the error message shown whenever our state is not satisfied
     * @return provided text, used for chaining
     */
    public static String hasText(String text, String message) {
        state(StringUtils.isNotBlank(text), message);
        return text;
    }

    private Asserts() {
    }

}
