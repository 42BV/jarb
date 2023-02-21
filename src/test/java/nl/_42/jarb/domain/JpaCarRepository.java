package nl._42.jarb.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
    public Car save(Car car) {
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
