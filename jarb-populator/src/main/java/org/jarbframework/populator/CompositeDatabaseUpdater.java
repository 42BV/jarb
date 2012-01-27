package org.jarbframework.populator;

import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database updater which can execute multiple updates in sequence.
 * @author Jeroen van Schagen
 * @since 01-06-2011
 */
public class CompositeDatabaseUpdater implements RevertableDatabaseUpdater {

    private final Logger logger = LoggerFactory.getLogger(CompositeDatabaseUpdater.class);

    /** Collection of update delegates, executed in order of collection. **/
    private final List<DatabaseUpdater> updaters;

    /**
     * Construct a new {@link CompositeDatabaseUpdater}.
     * @param updaters the updates that should be performed
     */
    public CompositeDatabaseUpdater(List<DatabaseUpdater> updaters) {
        this.updaters = Collections.unmodifiableList(updaters);
    }

    public static CompositeDatabaseUpdater composite(DatabaseUpdater... updaters) {
        return new CompositeDatabaseUpdater(Arrays.asList(updaters));
    }

    @Override
    public void update() {
        logger.info("Running {} composed updates...", updaters.size());
        for (DatabaseUpdater updater : updaters) {
            logger.info("Running update {}...", updater);
            updater.update();
        }
    }

    @Override
    public void revert() {
        logger.info("Reverting {} composed updates...", updaters.size());
        for (DatabaseUpdater updater : updatersInReversedOrder()) {
            if (updater instanceof RevertableDatabaseUpdater) {
                logger.info("Reverting update '{}'...", updater);
                ((RevertableDatabaseUpdater) updater).revert();
            } else {
                logger.info("Update '{}' was not revertable.", updater);
            }
        }
    }

    private List<DatabaseUpdater> updatersInReversedOrder() {
        List<DatabaseUpdater> reverse = new ArrayList<DatabaseUpdater>(updaters);
        Collections.reverse(reverse);
        return reverse;
    }

    @Override
    public String toString() {
        return "Composite(" + collectionToCommaDelimitedString(updaters) + ")";
    }
}
