package org.jarb.constraint.database.column;

public class UnknownTableException extends RuntimeException {
    private static final long serialVersionUID = 195207400829486389L;

    public UnknownTableException(String message) {
        super(message);
    }

}
