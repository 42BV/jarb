/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils;

/**
 * 
 *
 * @author jeroen
 * @since Apr 10, 2015
 */
public interface Predicate<T> {
    boolean apply(T input);
}
