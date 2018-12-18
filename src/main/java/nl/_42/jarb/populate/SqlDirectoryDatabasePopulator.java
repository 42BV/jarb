/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.populate;

import java.io.File;
import java.util.TreeSet;
import java.util.function.Predicate;

import javax.sql.DataSource;

import nl._42.jarb.utils.Asserts;
import nl._42.jarb.utils.predicates.AndPredicate;
import nl._42.jarb.utils.predicates.IsNotForOtherProduct;
import nl._42.jarb.utils.predicates.IsSqlFile;

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
        this(dataSource, baseDir, new AndPredicate<>(new IsSqlFile(), new IsNotForOtherProduct(dataSource)));
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
        TreeSet<String> paths = getFilePaths(baseDir);
        for (String path : paths) {
            new SqlDatabasePopulator(dataSource, path).execute();
        }
    }
    
    private TreeSet<String> getFilePaths(File directory) {
        TreeSet<String> paths = new TreeSet<>();
        for (String path : getSortedSubfiles(directory)) {
            File file = new File(path);
            if (file.isDirectory()) {
                paths.addAll(getFilePaths(file));
            } else if (isIncluded(file)) {
                paths.add(path);
            }
        }
        return paths;
    }
    
    private TreeSet<String> getSortedSubfiles(File directory) {
        TreeSet<String> found = new TreeSet<>();
        for (File subfile : directory.listFiles()) {
            found.add(subfile.getAbsolutePath());
        }
        return found;
    }

    private boolean isIncluded(File file) {
        if (predicate == null) {
            return true;
        }
        return predicate.test(file);
    }

}
