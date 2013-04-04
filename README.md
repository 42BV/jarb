Java Repository Bridge (JaRB)
=============================

JaRB aims to improve database usage in Java enterprise applications.

http://www.jarbframework.org

Features
--------
 * Database initialization
  * Automate database migrations on application startup
  * Populate database on application startup
   + SQL script based (using Spring JDBC)
   + Excel based
   + Building blocks: compound, conditional, fail-safe
 * (Database) constraints 
  * Automate database constraint validation with JSR303
  * Translate JDBC exceptions into constraint violation exceptions
   + Full access to constraint violation metadata
   + Map custom exceptions to named constraints
  * Describe bean constraint metadata, with content from JDBC and JSR303
  
Developers
----------
 * Jeroen van Schagen (jeroen@42.nl)
 
License
-------
 Copyright 2011 42bv (http://www.42.nl)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

Database migrations (schema)
----------------------------
By wrapping the data source in a migrating data source, the data base will
automatically be migrated to the latest version during application startup.

In the below example we use Liquibase to perform database migrations, by
default it will look for a 'src/main/db/changelog.groovy' file.


	<bean id="dataSource" class="org.jarbframework.migrations.MigratingDataSource">
	    <property name="delegate">
			<bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
			    <property name="driverClassName" value="org.hsqldb.jdbcDriver"/>
			    <property name="url" value="jdbc:hsqldb:mem:jarb"/>
			    <property name="username" value="sa"/>
			    <property name="password" value=""/>
			</bean>
		</property>
	    <property name="migrator">
	    	<bean class="org.jarbframework.migrations.liquibase.LiquibaseMigrator"/>
	    </property>
	</bean>


Database populating
-------------------
Whenever we require data to be inserted during application startup, the
database updater interface can be used. Below we demonstrate how to
insert data using an SQL script and Excel file.

	<bean class="org.jarbframework.populator.DatabaseUpdatingListener">
    	<constructor-arg name="initializer">
    	   <bean class="org.jarbframework.populator.CompositeDatabaseUpdater">
    	       <constructor-arg>
					<list>
						<bean class="org.jarbframework.populator.SqlResourceDatabaseUpdater">
							<property name="sqlResource" value="classpath:import.sql"/>
							<property name="dataSource" ref="dataSource"/>
						</bean>
						<bean class="org.jarbframework.populator.excel.ExcelDatabaseUpdater">
							<property name="excelResource" value="classpath:import.xls"/>
							<property name="entityManagerFactory" ref="entityManagerFactory"/>
						</bean>
						<bean class="org.jarbframework.populator.ClassNameDatabaseUpdater">
							<constructor-arg value="org.jarbframework.sample.PostUpdater"/>
						</bean>
					</list>
                </constructor-arg>
			</bean>
    	</constructor-arg>
    	<constructor-arg name="destroyer">
			<bean class="org.jarbframework.populator.SqlResourceDatabaseUpdater">
			    <property name="sqlResource" value="classpath:clean.sql"/>
			    <property name="dataSource" ref="dataSource"/>
			</bean>
    	</constructor-arg>
    </bean>

JSR303 database constraints
---------------------------
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

	<constraints:translate-exceptions data-source="dataSource" base-package="org.jarbframework.sample"/>

	@DatabaseConstraint("uk_posts_title")
	public class PostTitleAlreadyExistsException extends UniqueKeyViolationException {
		...
	}

Components
----------
 * constraints (simplifies the managing of (database) constraints by preventing and translating JDBC exceptions)
 * migrations (automated database migrations on application startup, with Liquibase implementation)
 * populator (populate the database with data on startup, SQL script implementation and building blocks)
 * populator-excel (excel driven database populator)
 * utils (common utility library, used by other components)