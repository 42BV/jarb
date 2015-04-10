/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator.predicates;

import org.jarbframework.utils.Predicate;

/**
 * Predicate that merges two others.
 *
 * @author Jeroen van Schagen
 * @since Apr 10, 2015
 */
public class AndPredicate<T> implements Predicate<T> {
    
    private final Predicate<T> left;
    
    private final Predicate<T> right;
    
    public AndPredicate(Predicate<T> left, Predicate<T> right) {
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(T input) {
        return left.apply(input) && right.apply(input);
    }
    
}
