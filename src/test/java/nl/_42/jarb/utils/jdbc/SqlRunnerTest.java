package nl._42.jarb.utils.jdbc;

import nl._42.jarb.utils.UtilsTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UtilsTestConfig.class)
public class SqlRunnerTest {

  @Autowired
  private DataSource dataSource;

  @Test
  public void set() {
    String sql = new SqlRunner(dataSource, "SELECT * FROM :table;").set("table", "dual").getSql();
    assertEquals("SELECT * FROM dual;", sql);
  }

  @Test
  public void set_expanded() {
    String sql = new SqlRunner(dataSource, "INSERT INTO persons (name) VALUES (':name');").set("name", "Jan-Willem de Boer 12_3*!").getSql();
    assertEquals("INSERT INTO persons (name) VALUES ('Jan-Willem de Boer 12_3*!');", sql);
  }

  @Test(expected = NullPointerException.class)
  public void set_empty() {
    new SqlRunner(dataSource, "SELECT * FROM :table;").set("table", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void set_sql_injection() {
    new SqlRunner(dataSource, "SELECT * FROM :table;").set("table", "dual; DROP TABLE persons");
  }

}
