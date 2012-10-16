package org.jarbframework.constraint.violation.domain;


/**
 * Holds user instances.
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public interface UserRepository {

    /**
     * Store a user in our repository.
     * @param user the user to add
     * @return added user instance
     */
    User add(User user);

    /**
     * Throws a checked exception.
     * @throws UserInactiveException always
     */
    void throwCheckedException() throws UserInactiveException;

    /**
     * Throws a runtime exception.
     * @throws UnsupportedOperationException always
     */
    void throwUnsupportedOperationException();

}
