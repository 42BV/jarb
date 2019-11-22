package nl._42.jarb.utils.jdbc;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * Execute SQL scripts. These could consist out of multiple SQL statements.
 */
@Component
public class SqlRunner {

  private static final String DEFAULT_ENCODING = "UTF-8";
  private static final String VALUE_REGEX = "^(.*(;|'|\").*$)";

  private final DataSource dataSource;
  private final String sql;

  public SqlRunner(DataSource dataSource, String sql) {
    this.dataSource = dataSource;
    this.sql = sql;
  }

  public SqlRunner set(String parameter, String value) {
    Objects.requireNonNull(value, "Value is required");
    if (value.matches(VALUE_REGEX)) {
      throw new IllegalArgumentException("Parameter values must be alphanumeric");
    }

    String replaced = sql.replaceAll(":" + parameter, "" + value);
    return new SqlRunner(dataSource, replaced);
  }

  /**
   * Execute the SQL statements, auto committing potential changes.
   */
  public void execute() {
    execute(true);
  }

  /**
   * Execute the SQL statements.
   * @param autoCommit if changes should be committed automatically. This
   *                   only applies when the data source has auto commit
   *                   set to {@code false}
   */
  public void execute(boolean autoCommit) {
    ResourceDatabasePopulator scripts = new ResourceDatabasePopulator();
    scripts.addScript(new InMemoryResource(sql));

    JdbcUtils.doWithConnection(dataSource, (connection) -> {
      scripts.populate(connection);
      return null;
    }, autoCommit);
  }

  //
  // Factories
  //

  public static SqlRunner load(DataSource dataSource, Resource resource) {
    return SqlRunner.load(dataSource, resource, DEFAULT_ENCODING);
  }

  public static SqlRunner load(DataSource dataSource, Resource resource, String encoding) {
    try (InputStream is = resource.getInputStream()) {
      return SqlRunner.load(dataSource, is, encoding);
    } catch (IOException ioe) {
      throw new IllegalStateException("Could not load template", ioe);
    }
  }

  public static SqlRunner load(DataSource dataSource, InputStream is) {
    return SqlRunner.load(dataSource, is, DEFAULT_ENCODING);
  }

  public static SqlRunner load(DataSource dataSource, InputStream is, String encoding) {
    String sql = toString(is, encoding);
    return new SqlRunner(dataSource, sql);
  }

  private static String toString(InputStream is, String encoding) {
    Scanner s = new Scanner(is, encoding).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public String getSql() {
    return sql;
  }

  private static class InMemoryResource extends AbstractResource {

    private final byte[] data;

    public InMemoryResource(String data) {
       this.data = data.getBytes();
    }

    @Override
    public String getDescription() {
      return "";
    }

    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(data);
    }

  }

}
