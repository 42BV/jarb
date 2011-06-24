Java Repository Bridge (JaRB)
=============================

JaRB aims to improve database usage in Java enterprise applications.

Developers
----------
 * Jeroen van Schagen (jeroen@42.nl)
 
License
-------
 Copyright [2011] [42BV]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

Features
--------
 * Database initialization
  * Automate database migrations on application startup
  * Populate database on application startup
   * SQL script based (using Spring JDBC)
   * Excel based
   * Building blocks: compound, conditional, fail-safe
 * (Database) constraints 
  * Automate database constraint validation with JSR303
  * Translate JDBC exceptions into constraint violation exceptions
   * Full access to constraint violation metadata
   * Map custom exceptions to named constraints
  * Describe bean constraint metadata, with content from JDBC and JSR303

Database migrations (schema)
----------------------------
TODO

Database populating
-------------------
TODO

JSR303 database constraints
---------------------------
TODO

Database constraint exceptions
------------------------------
TODO

Components
----------
 * constraint-metadata (describes bean constraint using atleast JDBC metadata and JSR303 annotations)
 * constraint-validation (automatic JSR303 database constraint validation)
 * constraint-violations (translates JDBC driver exceptions into constrain violation exceptions)
 * migrations (automated database migrations on application startup, with Liquibase implementation)
 * populator (populate the database with data on startup, SQL script implementation and building blocks)
 * populator-excel (excel driven database populator)
 * utils (common utility library, used by other components)