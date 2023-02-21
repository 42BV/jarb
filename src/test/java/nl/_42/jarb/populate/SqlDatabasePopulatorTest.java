package nl._42.jarb.populate;

import nl._42.jarb.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
public class SqlDatabasePopulatorTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Executing with a non-existing script resource causes a runtime exception to be thrown.
     */
    @Test
    public void testFailIfScriptNotFound() {
        try {
            new SqlDatabasePopulator(dataSource, new ClassPathResource("unknown.sql")).execute();
            Assertions.fail("Expected an exception because unknown.sql does not exist.");
        } catch (IllegalStateException e) {
            assertEquals("Resource 'unknown.sql' does not exist.", e.getMessage());
        }
    }

}
