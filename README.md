[![Java CI with Maven](https://github.com/42BV/jarb/actions/workflows/maven.yml/badge.svg)](https://github.com/42BV/jarb/actions/workflows/maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/97f8511219fe4fcbbeded9996166a126)](https://app.codacy.com/gh/42BV/jarb/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![codecov](https://codecov.io/gh/42BV/jarb/graph/badge.svg?token=WXpm6Dfj6r)](https://codecov.io/gh/42BV/jarb)
[![Maven Central](https://img.shields.io/maven-central/v/nl.42/jarb.svg?color=green)](https://central.sonatype.com/artifact/nl.42/jarb)
[![Javadocs](https://www.javadoc.io/badge/nl.42/jarb.svg)](https://www.javadoc.io/doc/nl.42/jarb)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Java Repository Bridge (JaRB)
=============================

JaRB aims to improve database usage in Java enterprise applications.

http://www.jarbframework.org

Features
--------
- Bean validation on database schema
    - Specify rules once in database
    - Publish constraints via REST
    - Honor constraints on frontend
- Translate JDBC driver specific exceptions into global constraint exceptions
    - Common exceptions regardless of driver
    - Access to constraint violation information
    - Map named constraint violations to project specific exceptions
- Populate database on application startup
    - SQL (file, directory)
    - Java based
    - Asyncronous

Developers
----------
- Jeroen van Schagen (jeroen@42.nl)
- Sander Benschop (sander@42.nl)
 
License
-------
 Copyright 2011-2019 42BV (http://www.42.nl)

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

    @DatabaseConstrained
    @Entity
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

The configuration is as follows:	

	@EnableDatabaseConstraints(basePackage = "nl._42.jarb.sample")

We also support XML configuration:

	<constraints:enable-constraints data-source="dataSource" base-package="nl._42.jarb.sample"/>
	<constraints:enable-constraints entity-manager-factory="entityManagerFactory" base-package="nl._42.jarb.sample"/>

Data insertion
-------------------
Whenever we require data to be inserted during application startup, the
database updater interface can be used. 

Below we demonstrate how to run an insert script at startup:

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

For XML configurations we provide a namespace:

    <populator:populate initializer="populator"/>
    
    <bean id="populator" class="nl._42.jarb.populator.SqlDatabasePopulator">
        <constructor-arg ref="dataSource"/>
        <constructor-arg value="classpath:import.sql"/>
    </bean>

Components
----------
 * constraints (simplifies the managing of (database) constraints by preventing and translating JDBC exceptions)
 * populator (populate the database with data on startup, SQL script implementation and building blocks)
 * utils (common utility library, used by other components)
