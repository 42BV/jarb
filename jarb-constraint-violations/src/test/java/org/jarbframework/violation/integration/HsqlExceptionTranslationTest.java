package org.jarbframework.violation.integration;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.sql.DataSource;

import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.jarbframework.violation.NotNullViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@IfProfileValue(name = "hsqldb.test", value = "true")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:hsql-context.xml", "classpath:translation-context.xml" })
public class HsqlExceptionTranslationTest {
    
    @Autowired
    private DatabaseConstraintExceptionTranslator exceptionTranslator;
    
    @Autowired
    private DataSource dataSource;
    
    private JdbcTemplate jdbcTemplate;
    
    @Before
    public void buildTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Test
    public void testNotNull() {
        try {
            jdbcTemplate.execute("INSERT INTO cars (price) VALUES (1.23)");
            fail("Expected an exception");
        } catch(RuntimeException rawException) {
            Throwable translatedException = exceptionTranslator.translateExceptionIfPossible(rawException);
            assertTrue(translatedException instanceof NotNullViolationException);
        }
    }
    
}
