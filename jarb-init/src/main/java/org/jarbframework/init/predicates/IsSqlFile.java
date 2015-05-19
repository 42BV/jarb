/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init.predicates;

import java.io.File;

import org.jarbframework.utils.Predicate;

/**
 * Checks if a file ends with SQL.
 *
 * @author Jeroen van Schagen
 * @since Apr 10, 2015
 */
public class IsSqlFile implements Predicate<File> {
    
    private static final String SQL_SUFFIX = ".sql";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(File input) {
        return input.getName().toLowerCase().endsWith(SQL_SUFFIX);
    }
    
}
