package org.jarbframework.populator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class RevertingDatabaseUpdaterTest {

    @Test
    public void testRun() {
        RevertableDatabaseUpdater delegate = mock(RevertableDatabaseUpdater.class);
        RevertingDatabaseUpdater updater = new RevertingDatabaseUpdater(delegate);

        updater.update();

        verify(delegate, times(1)).revert();
    }
}
