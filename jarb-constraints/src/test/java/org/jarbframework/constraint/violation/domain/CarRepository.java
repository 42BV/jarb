package org.jarbframework.constraint.violation.domain;

import org.jarbframework.constraint.domain.Car;


/**
 * Holds user instances.
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public interface CarRepository {

    /**
     * Store a car in our repository.
     * @param car the car to add
     * @return added car instance
     */
    Car add(Car user);

    /**
     * Throws a checked exception.
     * @throws CarInactiveException always
     */
    void throwCheckedException() throws CarInactiveException;

    /**
     * Throws a runtime exception.
     * @throws UnsupportedOperationException always
     */
    void throwUnsupportedOperationException();

}
