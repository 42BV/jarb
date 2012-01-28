package org.jarbframework.populator.excel.mapping;

/**
 * Thrown whenever a conversion could not be handled.
 * @author Jeroen van Schagen
 * @since 06-04-2011
 */
public class CouldNotConvertException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Object source;
    private final Class<?> sourceType;
    private final Class<?> targetType;

    public CouldNotConvertException(Object source, Class<?> sourceType, Class<?> targetType, Throwable cause) {
        super("Could not converter '" + source + "' [" + sourceType + "] into a " + targetType + ".", cause);
        this.source = source;
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public Object getSource() {
        return source;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

}
