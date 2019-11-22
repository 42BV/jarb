package nl._42.jarb.utils.jdbc;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

/**
 * Create SQL runner classes from a default template location.
 */
public class SqlTemplate {

  private static final String DEFAULT_BASE_PATH = "classpath:/sql";

  private final DataSource dataSource;
  private final ResourceLoader resourceLoader;

  private String basePath;

  public SqlTemplate(DataSource dataSource, ResourceLoader resourceLoader) {
    this.dataSource = dataSource;
    this.resourceLoader = resourceLoader;

    setBasePath(DEFAULT_BASE_PATH);
  }

  /**
   * Create a new SQL runner.
   * @param fileName the file name
   * @return the runner
   */
  public SqlRunner load(String fileName) {
    Resource resource = resourceLoader.getResource(basePath + fileName);
    return SqlRunner.load(dataSource, resource);
  }

  /**
   * Configure a base resource location. Missing trailing slashes will be added automatically.
   * @param basePath the base resource location, e.g. 'classpath:/sql'
   */
  public final void setBasePath(String basePath) {
    if (!basePath.endsWith("/")) {
      basePath += "/";
    }

    this.basePath = basePath;
  }

}
