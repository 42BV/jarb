package org.jarb.constraint.database.column;

public class UnknownColumnException extends RuntimeException {
    private static final long serialVersionUID = -3132978552231803686L;

    public UnknownColumnException(String message) {
        super(message);
    }

}
