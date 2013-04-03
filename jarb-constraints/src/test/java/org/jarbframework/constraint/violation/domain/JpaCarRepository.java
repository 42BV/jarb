package org.jarbframework.constraint.violation.domain;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jarbframework.constraint.domain.Car;
import org.springframework.stereotype.Repository;

/**
 * Java Persistence API implementation of {@link CarRepository}.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
@Repository
public class JpaCarRepository implements CarRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Car add(Car car) {
        entityManager.persist(car);
        return car;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void throwCheckedException() throws CarInactiveException {
        throw new CarInactiveException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException("Catch this");
    }

}
