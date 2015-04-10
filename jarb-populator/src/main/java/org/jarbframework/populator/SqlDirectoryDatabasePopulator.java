/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.populator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jarbframework.populator.predicates.AndPredicate;
import org.jarbframework.populator.predicates.IsNotForOtherProduct;
import org.jarbframework.populator.predicates.IsSqlFile;
import org.jarbframework.utils.Asserts;
import org.jarbframework.utils.Predicate;

/**
 * Populator that runs all files in a directory.
 *
 * @author Jeroen van Schagen
 * @since Apr 10, 2015
 */
public class SqlDirectoryDatabasePopulator implements DatabasePopulator {
    
    private final DataSource dataSource;

    private final File baseDir;
    
    private final Predicate<File> predicate;

    public SqlDirectoryDatabasePopulator(DataSource dataSource, File baseDir) {
        this(dataSource, baseDir, new AndPredicate<File>(new IsSqlFile(), new IsNotForOtherProduct(dataSource)));
    }

    public SqlDirectoryDatabasePopulator(DataSource dataSource, File baseDir, Predicate<File> predicate) {
        Asserts.notNull(dataSource, "Data source cannot be null.");
        Asserts.notNull(baseDir, "Base directory cannot be null.");
        Asserts.state(baseDir.isDirectory(), "Should be a directory.");
        
        this.dataSource = dataSource;
        this.baseDir = baseDir;
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate() {
        List<File> files = getFilesInDirectory(baseDir);
        for (File file : files) {
            new SqlDatabasePopulator(dataSource, file).populate();
        }
    }
    
    private List<File> getFilesInDirectory(File directory) {
        List<File> files = new ArrayList<File>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(getFilesInDirectory(file));
            } else if (isAllowed(file)) {
                files.add(file);
            }
        }
        return files;
    }
    
    private boolean isAllowed(File file) {
        if (predicate == null) {
            return true;
        }
        return predicate.apply(file);
    }

}
