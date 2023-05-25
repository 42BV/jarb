# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Update
- Updated to Spring Boot 3
- Updated to Hibernate Core 6.2.2.Final

## [5.3.0] - 20-05-2019
- Support @ElementCollection collection/map JPA mappings in bean meta-model
- Support javax validation annotations:
 * Size, NotEmpty, NotBlank, DecimalMin/Max, Negative, Positive, Email
- Simplified validation
 * Registering the database validator once during application start-up
 * Removed unused `DatabaseConstrained` properties: `id`, `factory`, `beanMetadataRepository`
- `DatabaseConstraintsConfigurer` changed from abstract class to interface, allows more flexibility in usage

## [5.2.0] - 06-05-2019
- Removed old XML support
- Speedup: Switched to JPA meta-model for entity detection 

## [5.1.1] - 02-05-2019
- Made optional `java el` dependency provided

## [5.1.0] - 01-04-2019
- JaRB can now translate Postgres exclusion errors.

## [5.0.1] - 2018-12-19

### Changed
- The project will be build with java 1.8 to keep backward compatibility with this jvm.

## [5.0.0.JAVA.11] - 2018-12-18

### Changed
- Rebranded from org.jarbframework to nl.42 groupId.
- Project dependencies upgraded to run on java 11
- Project build with java 11