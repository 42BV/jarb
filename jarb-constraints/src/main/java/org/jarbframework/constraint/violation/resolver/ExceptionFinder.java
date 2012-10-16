package org.jarbframework.constraint.violation.resolver;

/**
 * Capable of finding specific exceptions inside a hierarchy.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class ExceptionFinder {

    /**
     * Find the first exception of a specific type inside a hierarchy.
     * @param <T> type of exception
     * @param throwable starting throwable
     * @param exceptionType class of the exception type being found
     * @return first throwable instance of the specified type
     */
    @SuppressWarnings("unchecked")
    public static <T extends Exception> T findFirstException(Throwable throwable, Class<T> exceptionType) {
        Throwable current = throwable;
        while (current != null) {
            if (exceptionType.isInstance(current)) {
                return (T) current;
            }
            current = current.getCause();
        }
        return null; // No matches could be found
    }

    /**
     * Retrieve the initial throwable cause, furthest down inside the stack.
     * @param throwable starting throwable
     * @return initial throwable
     */
    public static Throwable getRootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private ExceptionFinder() {
    }

}
