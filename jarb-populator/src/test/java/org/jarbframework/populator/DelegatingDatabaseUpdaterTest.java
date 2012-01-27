package org.jarbframework.populator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;

public class DelegatingDatabaseUpdaterTest {

    @Test
    public void testUpdate() {
        DatabaseUpdater delegate = mock(DatabaseUpdater.class);
        DelegatingDatabaseUpdater updater = new DelegatingDatabaseUpdater(delegate);

        updater.update();

        verify(delegate, times(1)).update();
    }

    @Test
    public void testRevert() {
        RevertableDatabaseUpdater delegate = mock(RevertableDatabaseUpdater.class);
        DelegatingDatabaseUpdater updater = new DelegatingDatabaseUpdater(delegate);

        updater.revert();

        verify(delegate, times(1)).revert();
    }

    @Test
    public void testRevertUnsupported() {
        DatabaseUpdater delegate = mock(DatabaseUpdater.class);
        DelegatingDatabaseUpdater updater = new DelegatingDatabaseUpdater(delegate);

        updater.revert();

        verifyZeroInteractions(delegate);
    }

}
