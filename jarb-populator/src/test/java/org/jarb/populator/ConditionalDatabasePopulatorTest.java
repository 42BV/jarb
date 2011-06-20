package org.jarb.populator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

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
        ConditionalDatabasePopulator conditionalPopulator = new ConditionalDatabasePopulator() {

            @Override
            protected void doPopulate() throws Exception {
                populatorMock.populate();
            }

            @Override
            protected SupportsResult supports() {
                return SupportsResult.success();
            }

        };

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
        ConditionalDatabasePopulator conditionalPopulator = new ConditionalDatabasePopulator() {

            @Override
            protected void doPopulate() throws Exception {
                populatorMock.populate();
            }

            @Override
            protected SupportsResult supports() {
                return new SupportsResult()
                            .addFailure("State is always invalid, this is a test")
                            .addFailure("Second error message")
                            .addFailure("Last error message");
            }

        };

        EasyMock.replay(populatorMock);

        // Should not perform any populating
        conditionalPopulator.populate();

        // We can even recieve an exception about the invalid state
        conditionalPopulator.setThrowExceptionIfUnsupported(true);
        try {
            conditionalPopulator.populate();
            fail("Expected an illegal state exception as the populator is in an invalid state.");
        } catch (IllegalStateException e) {
            final String exceptionMessage = e.getMessage();
            assertTrue(exceptionMessage.contains("State is always invalid, this is a test"));
            assertTrue(exceptionMessage.contains("Second error message"));
            assertTrue(exceptionMessage.contains("Last error message"));
        }

        EasyMock.verify(populatorMock);
    }

}
