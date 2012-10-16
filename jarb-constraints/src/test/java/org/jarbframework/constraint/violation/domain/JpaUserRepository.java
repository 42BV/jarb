package org.jarbframework.constraint.violation.domain;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

/**
 * Java Persistence API implementation of {@link UserRepository}.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
@Repository
public class JpaUserRepository implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public User add(User user) {
        entityManager.persist(user);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void throwCheckedException() throws UserInactiveException {
        throw new UserInactiveException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException("Catch this");
    }

}
