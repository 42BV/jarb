package org.jarbframework.sample.model;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.UniqueKeyViolationException;
import org.jarbframework.constraint.violation.factory.NamedConstraint;

/**
 * Thrown whenever a post is being inserted with a title that already exists.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
@NamedConstraint("uk_posts_title")
public class PostTitleAlreadyExistsException extends UniqueKeyViolationException {

    public PostTitleAlreadyExistsException() {
    }

    /**
     * Construct a new {@link PostTitleAlreadyExistsException}.
     * 
     * @param violation constraint violation reference
     * @param cause the actual cause
     */
    public PostTitleAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause) {
        super(violation, "Post title already exists.", cause);
    }
    
}
