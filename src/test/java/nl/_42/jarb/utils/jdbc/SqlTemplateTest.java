package nl._42.jarb.utils.jdbc;

import nl._42.jarb.utils.UtilsTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UtilsTestConfig.class)
public class SqlTemplateTest {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private ResourceLoader resourceLoader;

  private SqlTemplate sqlTemplate;

  @Before
  public void setUp() {
    sqlTemplate = new SqlTemplate(dataSource, resourceLoader);
  }

  @Test
  public void execute() throws SQLException {
    sqlTemplate.load("create_persons.sql").set("name", "eddie").set("unknown", "ignore").execute();
    sqlTemplate.load("copy_persons.sql").execute();

    ResultSet rs = dataSource.getConnection().createStatement().executeQuery("select name from persons");
    assertTrue(rs.next());
    assertEquals("eddie", rs.getString("name"));

    assertTrue(rs.next());
    assertEquals("eddie", rs.getString("name"));

    assertFalse(rs.next());
  }

}
