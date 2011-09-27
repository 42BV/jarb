package org.jarbframework.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.jarbframework.populator.ConditionalDatabasePopulator;
import org.jarbframework.populator.DatabasePopulator;
import org.jarbframework.populator.condition.Condition;
import org.jarbframework.populator.condition.ResourceExistsCondition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ConditionalDatabasePopulatorTest {
    private DatabasePopulator populatorMock;

    @Before
    public void setUp() {
        populatorMock = EasyMock.createMock(DatabasePopulator.class);
    }

    /**
     * Ensure that we populate whenever the state is supported.
     */
    @Test
    public void testSupported() throws Exception {
        final Condition existingResourceExists = new ResourceExistsCondition(new ClassPathResource("create-schema.sql"));
        assertTrue(existingResourceExists.check().isSatisfied()); // Resource 'create-schema.sql' exists on our classpath
        
        ConditionalDatabasePopulator conditionalPopulator = new ConditionalDatabasePopulator(populatorMock, existingResourceExists);

        populatorMock.populate();
        EasyMock.expectLastCall();
        EasyMock.replay(populatorMock);

        conditionalPopulator.populate();

        EasyMock.verify(populatorMock);
    }

    /**
     * Whenever our state is unsupported, no populate should happen.
     */
    @Test
    public void testUnsupported() throws Exception {
        final Condition unknownResourceDoesNotExists = new ResourceExistsCondition(new ClassPathResource("unknown.sql"));
        assertFalse(unknownResourceDoesNotExists.check().isSatisfied()); // Resource 'unknown.sql' does not exist on our classpath
        
        ConditionalDatabasePopulator conditionalPopulator = new ConditionalDatabasePopulator(populatorMock, unknownResourceDoesNotExists);

        EasyMock.replay(populatorMock);

        // Should not perform any populating, because the condition is not satisfied
        conditionalPopulator.populate();

        // We can even recieve an exception about the unsatisfied condition
        conditionalPopulator.setThrowExceptionIfUnsupported(true);
        try {
            conditionalPopulator.populate();
            fail("Expected an illegal state exception because our condition was not satisfied.");
        } catch (IllegalStateException e) {
            assertEquals(
                    "Database populator (" + populatorMock + ") was not executed, because:\n" +
                    " - Resource 'class path resource [unknown.sql]' does not exist."
                    , e.getMessage()
            );
        }

        EasyMock.verify(populatorMock);
    }

}
