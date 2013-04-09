package org.jarbframework.constraint.violation.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.mapping.NamedConstraint;
import org.jarbframework.constraint.violation.factory.mapping.NameMatchingStrategy;
import org.jarbframework.constraint.violation.factory.mapping.NamePredicate;
import org.jarbframework.utils.Classes;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * Configurable constraint violation exception factory. Enables users
 * to define custom exception factories for a specific constraint.
 * Whenever no custom exception factory could be found, we use a default
 * exception factory. 
 * 
 * @author Jeroen van Schagen
 * @since 12-05-2011
 */
public class ConfigurableConstraintExceptionFactory implements DatabaseConstraintExceptionFactory {
    
    /** Registered custom exception factories. **/
    private final List<CustomFactoryMapping> customFactoryMappings = new ArrayList<CustomFactoryMapping>();
    
    /** Factory used whenever no custom factory could be determined. **/
    private final DatabaseConstraintExceptionFactory defaultFactory;

    public ConfigurableConstraintExceptionFactory() {
        this(new TypeBasedConstraintExceptionFactory());
    }

    public ConfigurableConstraintExceptionFactory(DatabaseConstraintExceptionFactory defaultFactory) {
        this.defaultFactory = Preconditions.checkNotNull(defaultFactory, "Default violation exception factory cannot be null.");
    }

    @Override
    public Throwable buildException(DatabaseConstraintViolation violation, Throwable cause) {
        Preconditions.checkNotNull(violation, "Cannot create exception for a null violation.");
        return findFactoryFor(violation).buildException(violation, cause);
    }

    /**
     * Retrieve the exception factory for a specific constraint. Whenever no
     * matching factory could we found, we return the default factory.
     * @param violation reference to our constraint violation
     * @return factory capable of building a violation exception for our constraint
     */
    private DatabaseConstraintExceptionFactory findFactoryFor(DatabaseConstraintViolation violation) {
        DatabaseConstraintExceptionFactory resultFactory = defaultFactory;
        for (CustomFactoryMapping customFactoryMapping : customFactoryMappings) {
            if (customFactoryMapping.isSupported(violation)) {
                resultFactory = customFactoryMapping.getExceptionFactory();
                break;
            }
        }
        return resultFactory;
    }

    /**
     * Register a custom exception for a constraint.
     * @param constraintName name of the constraint
     * @param matchingStrategy strategy used for matching
     * @param exceptionClass class of the exception (factory)
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(String constraintName, NameMatchingStrategy matchingStrategy, Class<?> exceptionClass) {
        ReflectionConstraintExceptionFactory exceptionFactory = new ReflectionConstraintExceptionFactory(exceptionClass);
        return register(constraintName, matchingStrategy, exceptionFactory);
    }
    
    /**
     * Register a custom exception factory for a constraint.
     * @param constraintName name of the constraint
     * @param matchingStrategy strategy used for matching
     * @param exceptionFactory creates the exception
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(String constraintName, NameMatchingStrategy matchingStrategy, DatabaseConstraintExceptionFactory exceptionFactory) {
        NamePredicate violationPredicate = new NamePredicate(constraintName, matchingStrategy);
        return register(violationPredicate, exceptionFactory);
    }

    /**
     * Register an exception factory for database constraints that match a certain criteria.
     * @param violationPredicate describes the criteria that our constraints must match
     * @param exceptionFactory the exception factory that should be used
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(Predicate<DatabaseConstraintViolation> violationPredicate, DatabaseConstraintExceptionFactory exceptionFactory) {
        CustomFactoryMapping customFactoryMapping = new CustomFactoryMapping(violationPredicate, exceptionFactory);
        customFactoryMappings.add(customFactoryMapping);
        return this;
    }

    /**
     * Register all {@code @DatabaseConstraint} annotated exception (factories) inside
     * the specified base package. Use this method to quickly register all exceptions.
     * @param basePackage the base package to search in
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory registerAll(String basePackage) {
        Set<Class<?>> annotatedClasses = Classes.getAllWithAnnotation(basePackage, NamedConstraint.class);
        for(Class<?> annotatedClass : annotatedClasses) {
            NamedConstraint annotation = AnnotationUtils.findAnnotation(annotatedClass, NamedConstraint.class);
            register(annotation.value(), annotation.strategy(), annotatedClass);
        }
        
        return this;
    }
    
    private static class CustomFactoryMapping {
        
        private final Predicate<DatabaseConstraintViolation> violationPredicate;
        
        private final DatabaseConstraintExceptionFactory exceptionFactory;

        public CustomFactoryMapping(Predicate<DatabaseConstraintViolation> violationPredicate, DatabaseConstraintExceptionFactory exceptionFactory) {
            this.violationPredicate = Preconditions.checkNotNull(violationPredicate, "Violation predicate cannot be null.");
            this.exceptionFactory = Preconditions.checkNotNull(exceptionFactory, "Exception factory cannot be null.");
        }

        public boolean isSupported(DatabaseConstraintViolation violation) {
            return violationPredicate.apply(violation);
        }

        public DatabaseConstraintExceptionFactory getExceptionFactory() {
            return exceptionFactory;
        }
        
    }

}
