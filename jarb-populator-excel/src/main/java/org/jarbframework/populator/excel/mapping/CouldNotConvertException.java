package org.jarbframework.populator.excel.mapping;

/**
 * Thrown whenever a conversion could not be handled.
 * @author Jeroen van Schagen
 * @since 06-04-2011
 */
public class CouldNotConvertException extends RuntimeException {
    private static final long serialVersionUID = 7220449642498788002L;
    private final Object source;
    private final Class<?> targetType;

    public CouldNotConvertException(Object source, Class<?> targetType, Throwable cause) {
        super("Could not converter '" + source + "' [" + source.getClass().getSimpleName() + "] into a " + targetType.getClass().getSimpleName() + ".", cause);
        this.source = source;
        this.targetType = targetType;
    }

    public Object getSource() {
        return source;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

}
