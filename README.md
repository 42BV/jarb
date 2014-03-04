Java Repository Bridge (JaRB)
=============================

JaRB aims to improve database usage in Java enterprise applications.

http://www.jarbframework.org

Features
--------
 * Database constraints
  * Automate database schema validation with JSR303
  * Translate JDBC driver exceptions into constraint exceptions
   + Full access to constraint violation information
   + Map custom exceptions to named constraints
  * Describe bean constraint metadata, with front-end example
 * Database initialization
  * Automate database migrations on application startup
  * Populate database on application startup
   + SQL
   + Excel

Developers
----------
 * Jeroen van Schagen (jeroen@42.nl)
 
License
-------
 Copyright 2011-2014 42BV (http://www.42.nl)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

Database constraints
--------------------

Validation
----------
To validate database constraints with JSR303 validation, we often need to
duplicate constraint information in both the database and entity class.
Duplication is never good, so we made a @DatabaseConstrained annotation that
dynamically validates all simple database constraints based on JDBC metadata.

  @DatabaseConstrained @Entity
  public class Person {
    @Id @GeneratedValue
    private Long id;
    private String name;
    ...
  }

Database constraint exceptions
------------------------------
Whenever a database constraint is violated, the JDBC driver will convert it
into a runtime exception. This SQLException is hard to use in the application
because all metadata is held inside its message. By using exception translation
we can convert the driver exception into a more intuitive exception, e.g. the
UniqueKeyAlreadyExistsException. Inside our translated exception we have full
access to the constraint violation and any desired metadata.

It is even possible to map custom exceptions on named constraints.

	@NamedConstraint("uk_posts_title")
	public class PostTitleAlreadyExistsException extends UniqueKeyViolationException {
		...
	}
	
Configuration
-------------

The XML configuration is as follows:	

	<constraints:enable-constraints data-source="dataSource" base-package="org.jarbframework.sample"/>
	<constraints:enable-constraints entity-manager-factory="entityManagerFactory" base-package="org.jarbframework.sample"/>

We also support Java configuration:

	@EnableDatabaseConstraints(basePackage = "org.jarbframework.sample")

Database migrations (schema)
----------------------------
By wrapping the data source in a migrating data source, the data base will
automatically be migrated to the latest version during application startup.

In the below example we use Liquibase to perform database migrations, by
default it will look for a 'src/main/db/changelog.groovy' file.


	<migrations:migrate data-source="dataSource" path="src/main/db/changelog.groovy"/>

Or an embedded database:

    @Bean
    public DataSource dataSource() {
        return new EmbeddedMigratingDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }

Database populating
-------------------
Whenever we require data to be inserted during application startup, the
database updater interface can be used. Below we demonstrate how to
insert data using an SQL script and Excel file.

    @Bean
    public PopulateApplicationListener populateAppliationListener() {
        return new PopulateApplicationListenerBuilder()
                        .initializer()
                            .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("import.sql")))
                            .add(new ExcelDatabasePopulator(entityManagerFactory, new ClassPathResource("import.xls")))
                            .add(postPopulator())
                            .done()
                        .destroyer()
                            .add(new SqlDatabasePopulator(dataSource, new ClassPathResource("clean.sql")))
                   .build();
    }

Components
----------
 * constraints (simplifies the managing of (database) constraints by preventing and translating JDBC exceptions)
 * migrations (automated database migrations on application startup, with Liquibase implementation)
 * populator (populate the database with data on startup, SQL script implementation and building blocks)
 * populator-excel (excel driven database populator)
 * utils (common utility library, used by other components)
 * sample (demonstrates all above described functionality in a simple project)

Deployment
----------
mvn deploy -DaltDeploymentRepository=repository.42.nl::default::https://repository.42.nl/content/repositories/thirdparty
 