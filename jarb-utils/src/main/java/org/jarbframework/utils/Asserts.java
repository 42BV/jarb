package org.jarbframework.utils;


/**
 * Performs precondition checks on the code.
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
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
     * @param object the object to check
     * @param message the error message shown whenever our state is not satisfied
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

    /**
     * Assert that an object is the instance of a specific class.
     * @param object the object to check the instance of
     * @param type expected instance type
     * @param message the error message shown whenever our state is not satisfied
     * @return provided object, used for chaining
     */
    @SuppressWarnings("unchecked")
    public static <T> T instanceOf(Object object, Class<T> type, String message) {
        state(notNull(type, "Expected type cannot be null").isInstance(object), message);
        return (T) object;
    }

    private Asserts() {
    }

}
