package org.jarb.populator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.init.DatabasePopulator;

public class ConditionalDatabasePopulatorTest {
    private DatabasePopulator databasePopulatorMock;
    
    @Before
    public void setUp() {
        databasePopulatorMock = EasyMock.createMock(DatabasePopulator.class);
    }

    /**
     * Ensure that our {@link ConditionalDatabasePopulator#doPopulate(Connection)}
     * is invoked whenever the desired precondition is met.
     */
    @Test
    public void testSupported() throws SQLException {
        ConditionalDatabasePopulator conditionalPopulator = new ConditionalDatabasePopulator() {
          
            @Override
            protected void doPopulate(Connection connection) throws SQLException {
                databasePopulatorMock.populate(connection);
            }
            
            @Override
            protected SupportsResult supports(Connection connection) {
                return SupportsResult.success();
            }
            
        };
        
        final Connection connection = EasyMock.createMock(Connection.class);
        
        databasePopulatorMock.populate(connection);
        EasyMock.expectLastCall();
        EasyMock.replay(databasePopulatorMock);
        
        conditionalPopulator.populate(connection);
        
        EasyMock.verify(databasePopulatorMock);
    }
    
    /**
     * If the precondition is not met, we should perform no database populating, and
     * should recieve information about the unsatisfied precondition.
     */
    @Test
    public void testUnsupported() throws SQLException {
        ConditionalDatabasePopulator conditionalPopulator = new ConditionalDatabasePopulator() {
            
            @Override
            protected void doPopulate(Connection connection) throws SQLException {
                databasePopulatorMock.populate(connection);
            }
            
            @Override
            protected SupportsResult supports(Connection connection) {
                return new SupportsResult()
                            .addFailure("Precondition can never succeed")
                            .addFailure("Second error message")
                            .addFailure("Last error message");
            }
            
        };
        
        final Connection connection = EasyMock.createMock(Connection.class);
        
        EasyMock.replay(databasePopulatorMock);
        
        // Database population will not take place as the precondition is not met
        conditionalPopulator.populate(connection);
        
        // We can even recieve an exception about the invalid precondition
        conditionalPopulator.setThrowErrorOnFailure(true);
        try {
            conditionalPopulator.populate(connection);
            fail("Expected an illegal state exception as the populator is in an invalid precondition.");
        } catch(IllegalStateException e) {
            final String message = e.getMessage();
            assertTrue(message.startsWith("Database populator was skipped as precondition is not met:"));
            assertTrue(message.contains("Precondition can never succeed"));
            assertTrue(message.contains("Second error message"));
            assertTrue(message.contains("Last error message"));
        }
        
        EasyMock.verify(databasePopulatorMock);
    }
    
}
