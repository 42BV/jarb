package org.jarbframework.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.jarbframework.populator.condition.Condition;
import org.jarbframework.populator.condition.ConditionalDatabaseUpdater;
import org.jarbframework.populator.condition.ResourceExistsCondition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ConditionalDatabaseUpdaterTest {
    private DatabaseUpdater mockedUpdater;

    @Before
    public void setUp() {
        mockedUpdater = EasyMock.createMock(DatabaseUpdater.class);
    }

    /**
     * Ensure that we populate whenever the state is supported.
     */
    @Test
    public void testSupported() throws Exception {
        final Condition existingResourceExists = new ResourceExistsCondition(new ClassPathResource("create-schema.sql"));
        assertTrue(existingResourceExists.evaluate().isSatisfied()); // Resource 'create-schema.sql' exists on our classpath
        
        ConditionalDatabaseUpdater conditionalUpdater = new ConditionalDatabaseUpdater(mockedUpdater, existingResourceExists);

        mockedUpdater.update();
        EasyMock.expectLastCall();
        EasyMock.replay(mockedUpdater);

        conditionalUpdater.update();

        EasyMock.verify(mockedUpdater);
    }

    /**
     * Whenever our state is unsupported, no populate should happen.
     */
    @Test
    public void testUnsupported() throws Exception {
        final Condition unknownResourceDoesNotExists = new ResourceExistsCondition(new ClassPathResource("unknown.sql"));
        assertFalse(unknownResourceDoesNotExists.evaluate().isSatisfied()); // Resource 'unknown.sql' does not exist on our classpath
        
        ConditionalDatabaseUpdater conditionalUpdater = new ConditionalDatabaseUpdater(mockedUpdater, unknownResourceDoesNotExists);

        EasyMock.replay(mockedUpdater);

        // Should not perform any populating, because the condition is not satisfied
        conditionalUpdater.update();

        // We can even recieve an exception about the unsatisfied condition
        conditionalUpdater.setThrowExceptionIfUnsupported(true);
        try {
            conditionalUpdater.update();
            fail("Expected an illegal state exception because our condition was not satisfied.");
        } catch (IllegalStateException e) {
            assertEquals(
                    "Update (" + mockedUpdater + ") was not performed, because:\n" +
                    " - Resource 'class path resource [unknown.sql]' does not exist."
                    , e.getMessage()
            );
        }

        EasyMock.verify(mockedUpdater);
    }

}
