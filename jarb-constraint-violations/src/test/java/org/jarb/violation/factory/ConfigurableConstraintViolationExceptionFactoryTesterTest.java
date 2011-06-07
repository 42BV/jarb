package org.jarb.violation.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.jarb.constraint.database.named.NamedConstraintMetadata;
import org.jarb.constraint.database.named.NamedConstraintMetadataRepository;
import org.jarb.constraint.database.named.NamedConstraintType;
import org.junit.Before;
import org.junit.Test;

public class ConfigurableConstraintViolationExceptionFactoryTesterTest {
    private ConfigurableConstraintViolationExceptionFactoryTester tester;
    private NamedConstraintMetadataRepository constraintMetadataRepositoryMock;

    @Before
    public void setUp() {
        constraintMetadataRepositoryMock = EasyMock.createMock(NamedConstraintMetadataRepository.class);
        tester = new ConfigurableConstraintViolationExceptionFactoryTester(constraintMetadataRepositoryMock);
    }

    /**
     * Register each constraint with a custom exception factory, the tester be satisfied.
     */
    @Test
    public void testAllConstraintsRegistered() {
        NamedConstraintMetadata namedConstraint = new NamedConstraintMetadata("pk_cars_id", NamedConstraintType.PRIMARY_KEY);
        EasyMock.expect(constraintMetadataRepositoryMock.all()).andReturn(Arrays.asList(namedConstraint));
        EasyMock.replay(constraintMetadataRepositoryMock);

        ConfigurableConstraintViolationExceptionFactory exceptionFactory = new ConfigurableConstraintViolationExceptionFactory();
        exceptionFactory.registerFactory("pk_cars_id", new SimpleConstraintViolationExceptionFactory());

        tester.assertAllNamedConstraintsAreRegistered(exceptionFactory);

        EasyMock.verify(constraintMetadataRepositoryMock);
    }

    /**
     * Forgot to register a named constraint, the tester should fail.
     */
    @Test
    public void testNamedConstraintNotRegistered() {
        NamedConstraintMetadata namedConstraint = new NamedConstraintMetadata("pk_cars_id", NamedConstraintType.PRIMARY_KEY);
        EasyMock.expect(constraintMetadataRepositoryMock.all()).andReturn(Arrays.asList(namedConstraint));
        EasyMock.replay(constraintMetadataRepositoryMock);

        ConfigurableConstraintViolationExceptionFactory exceptionFactory = new ConfigurableConstraintViolationExceptionFactory();

        try {
            tester.assertAllNamedConstraintsAreRegistered(exceptionFactory);
            fail("Not all constraints are registered, expecting an exception");
        } catch (AssertionError e) {
            assertEquals("These named constraints need to be registered: [pk_cars_id]", e.getMessage());
        }

        EasyMock.verify(constraintMetadataRepositoryMock);
    }

    /**
     * Each registered constraint is known in the database, tester should be satisfied.
     */
    @Test
    public void testAllRegisteredConstraintsKnown() {
        NamedConstraintMetadata namedConstraint = new NamedConstraintMetadata("pk_cars_id", NamedConstraintType.PRIMARY_KEY);
        EasyMock.expect(constraintMetadataRepositoryMock.all()).andReturn(Arrays.asList(namedConstraint));
        EasyMock.replay(constraintMetadataRepositoryMock);

        ConfigurableConstraintViolationExceptionFactory exceptionFactory = new ConfigurableConstraintViolationExceptionFactory();
        exceptionFactory.registerFactory("pk_cars_id", new SimpleConstraintViolationExceptionFactory());

        tester.assertAllRegisteredConstraintsExist(exceptionFactory);

        EasyMock.verify(constraintMetadataRepositoryMock);
    }

    /**
     * Registered an invalid (not known) constraint, tester should fail.
     */
    @Test
    public void testInvalidConstraintRegistered() {
        NamedConstraintMetadata namedConstraint = new NamedConstraintMetadata("pk_cars_id", NamedConstraintType.PRIMARY_KEY);
        EasyMock.expect(constraintMetadataRepositoryMock.all()).andReturn(Arrays.asList(namedConstraint));
        EasyMock.replay(constraintMetadataRepositoryMock);

        ConfigurableConstraintViolationExceptionFactory exceptionFactory = new ConfigurableConstraintViolationExceptionFactory();
        exceptionFactory.registerFactory("invalid_constraint", new SimpleConstraintViolationExceptionFactory());

        try {
            tester.assertAllRegisteredConstraintsExist(exceptionFactory);
            fail("Not all constraints are registered, expecting an exception");
        } catch (AssertionError e) {
            assertEquals("These named constraints do not exist in our database: [invalid_constraint]", e.getMessage());
        }

        EasyMock.verify(constraintMetadataRepositoryMock);
    }

}
