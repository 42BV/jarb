package org.jarb.violation.domain;

/**
 * Holds car instances.
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public interface CarRepository {

    /**
     * Store a car in our repository.
     * @param car the car to add
     * @return added car instance
     */
    Car add(Car car);

    /**
     * Throws a checked exception.
     * @throws GarageClosedException always
     */
    void throwCheckedException() throws GarageClosedException;

    /**
     * Throws a runtime exception.
     * @throws UnsupportedOperationException always
     */
    void throwUnsupportedOperationException();

}
