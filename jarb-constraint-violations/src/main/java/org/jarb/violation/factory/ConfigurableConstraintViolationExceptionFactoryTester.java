package org.jarb.violation.factory;

import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

import java.util.HashSet;
import java.util.Set;

import org.jarb.constraint.database.named.NamedConstraintMetadata;
import org.jarb.constraint.database.named.NamedConstraintMetadataRepository;

/**
 * Verifies the state of a configurable constraint violation exception factory.
 * 
 * @author Jeroen van Schagen
 * @since 25-05-2011
 */
public class ConfigurableConstraintViolationExceptionFactoryTester {
    private final NamedConstraintMetadataRepository constraintMetadataRepository;

    /**
     * Construct a new {@link ConfigurableConstraintViolationExceptionFactoryTester}.
     * @param constraintMetadataRepository retrieves named constraint metadata from database
     */
    public ConfigurableConstraintViolationExceptionFactoryTester(NamedConstraintMetadataRepository constraintMetadataRepository) {
        this.constraintMetadataRepository = constraintMetadataRepository;
    }

    /**
     * Tests that all named constraints, known in the database, have a custom constraint exception factory.
     * @param exceptionFactory the custom exception factory to check on
     */
    public void assertAllNamedConstraintsAreRegistered(ConfigurableConstraintViolationExceptionFactory exceptionFactory) {
        Set<String> unregisteredNamedConstraints = new HashSet<String>();
        Set<String> registeredConstraintNames = exceptionFactory.getFactories().keySet();
        for (String constraintName : getDeclaredConstraintNames()) {
            if (!registeredConstraintNames.contains(constraintName)) {
                unregisteredNamedConstraints.add(constraintName);
            }
        }
        if (!unregisteredNamedConstraints.isEmpty()) {
            throw new AssertionError("These named constraints need to be registered: [" + collectionToCommaDelimitedString(unregisteredNamedConstraints) + "]");
        }
    }

    /**
     * Tests that all registered named constraints are known in the database.
     * @param exceptionFactory the custom exception factory to check on
     */
    public void assertAllRegisteredConstraintsExist(ConfigurableConstraintViolationExceptionFactory exceptionFactory) {
        Set<String> invalidConstraintNames = new HashSet<String>();
        Set<String> validConstraintNames = getDeclaredConstraintNames();
        for (String constraintName : exceptionFactory.getFactories().keySet()) {
            if (!validConstraintNames.contains(constraintName)) {
                invalidConstraintNames.add(constraintName);
            }
        }
        if (!invalidConstraintNames.isEmpty()) {
            throw new AssertionError("These named constraints do not exist in our database: [" + collectionToCommaDelimitedString(invalidConstraintNames) + "]");
        }
    }

    // Collect all named constraints declared inside our database
    private Set<String> getDeclaredConstraintNames() {
        Set<String> constraintNames = new HashSet<String>();
        for (NamedConstraintMetadata constraintMetadata : constraintMetadataRepository.all()) {
            constraintNames.add(constraintMetadata.getName());
        }
        return constraintNames;
    }

}
