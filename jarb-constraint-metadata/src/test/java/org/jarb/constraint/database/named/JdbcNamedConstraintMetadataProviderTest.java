package org.jarb.constraint.database.named;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class JdbcNamedConstraintMetadataProviderTest {
    private JdbcNamedConstraintMetadataProvider constraintMetadataProvider;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        constraintMetadataProvider = new JdbcNamedConstraintMetadataProvider(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("INSERT INTO named_constraint_metadata (name,type) VALUES ('pk_cars_id','PRIMARY_KEY')");
    }

    @After
    public void tearDown() {
        jdbcTemplate.execute("DELETE FROM named_constraint_metadata WHERE name = 'pk_cars_id'");
    }

    @Test
    public void testProvideConstraint() {
        Set<NamedConstraintMetadata> constraintMetadataSet = constraintMetadataProvider.all();
        assertEquals(1, constraintMetadataSet.size());
        NamedConstraintMetadata constraintMetadata = constraintMetadataSet.iterator().next();
        assertEquals("pk_cars_id", constraintMetadata.getName());
        assertEquals(NamedConstraintType.PRIMARY_KEY, constraintMetadata.getType());
    }

}
