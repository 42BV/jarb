/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.init.populate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.jarbframework.init.predicates.AndPredicate;
import org.jarbframework.init.predicates.IsNotForOtherProduct;
import org.jarbframework.init.predicates.IsSqlFile;
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
    public void execute() {
        List<File> files = getFilesInDirectory(baseDir);
        for (File file : files) {
            new SqlDatabasePopulator(dataSource, file).execute();
        }
    }
    
    private List<File> getFilesInDirectory(File directory) {
        List<File> files = new ArrayList<>();
        for (File file : getSortedSubfiles(directory)) {
            if (file.isDirectory()) {
                files.addAll(getFilesInDirectory(file));
            } else if (isAllowed(file)) {
                files.add(file);
            }
        }
        return files;
    }
    
    private List<File> getSortedSubfiles(File directory) {
        TreeSet<String> found = new TreeSet<>();
        for (File subfile : directory.listFiles()) {
            found.add(subfile.getAbsolutePath());
        }
        return found.stream().map(name -> new File(name)).collect(Collectors.toList());
    }

    private boolean isAllowed(File file) {
        if (predicate == null) {
            return true;
        }
        return predicate.apply(file);
    }

}
