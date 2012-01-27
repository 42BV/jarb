package org.jarbframework.populator.condition;

import static org.jarbframework.populator.condition.Condition.ConditionEvaluation.sucess;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jarbframework.populator.DatabaseUpdater;
import org.junit.Before;
import org.junit.Test;

public class ConditionalDatabaseUpdaterTest {

    private DatabaseUpdater delegate;

    @Before
    public void setUp() {
        delegate = mock(DatabaseUpdater.class);
    }

    @Test
    public void testRun() {
        Condition always = new Condition() {

            @Override
            public ConditionEvaluation evaluate() {
                return sucess();
            }

        };

        ConditionalDatabaseUpdater updater = new ConditionalDatabaseUpdater(delegate, always);
        updater.update();

        verify(delegate, times(1)).update();
    }

    @Test
    public void testSkip() {
        ConditionalDatabaseUpdater updater = skippingUpdater();
        updater.update();

        verify(delegate, never()).update();
    }

    private ConditionalDatabaseUpdater skippingUpdater() {
        Condition never = new Condition() {

            @Override
            public ConditionEvaluation evaluate() {
                return new ConditionEvaluation().addFailure("Never succeed");
            }

        };

        return new ConditionalDatabaseUpdater(delegate, never);
    }

    @Test(expected = IllegalStateException.class)
    public void testSkipWithException() {
        ConditionalDatabaseUpdater updater = skippingUpdater();
        updater.setThrowExceptionIfUnsupported(true);
        updater.update();
    }

}
