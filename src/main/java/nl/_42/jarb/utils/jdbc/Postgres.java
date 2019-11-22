package nl._42.jarb.utils.jdbc;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utilities for Postgres.
 */
public class Postgres {

  /**
   * Execute the SQL statements and output the results into a string.
   * @param runner the SQL runner
   * @param writer the writer
   * @throws IOException whenever an exception occurs
   */
  public void copy(SqlRunner runner, Writer writer) throws IOException {
    copy(runner.getDataSource(), runner.getSql(), writer);
  }

  /**
   * Execute the SQL statements and output results to writer.
   * This method should only be used for select queries, changes
   * will not be committed unless the data source has auto commit
   * set to {@code true}.
   * @param dataSource the data source
   * @param sql the SQL statements
   * @param writer the writer
   * @throws IOException whenever an exception occurs
   */
  public void copy(DataSource dataSource, String sql, Writer writer) throws IOException {
    try (Connection connection = dataSource.getConnection()) {
      CopyManager manager = connection.unwrap(BaseConnection.class).getCopyAPI();
      manager.copyOut(sql, writer);
    } catch (SQLException e) {
      throw new IllegalStateException("Could not execute SQL", e);
    }
  }

}
