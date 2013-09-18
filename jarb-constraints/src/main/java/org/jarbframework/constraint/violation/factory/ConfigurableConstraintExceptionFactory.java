package org.jarbframework.constraint.violation.factory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.reflection.ReflectionConstraintExceptionFactory;
import org.jarbframework.utils.Classes;
import org.springframework.core.annotation.AnnotationUtils;

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
    private final List<ExceptionFactoryMapping> exceptionFactoryMappings = new ArrayList<ExceptionFactoryMapping>();
    
    /** Factory used whenever no custom factory could be determined. **/
    private final DatabaseConstraintExceptionFactory defaultExceptionFactory;

    public ConfigurableConstraintExceptionFactory() {
        this(null);
    }

    public ConfigurableConstraintExceptionFactory(DatabaseConstraintExceptionFactory defaultExceptionFactory) {
    	this.defaultExceptionFactory = defaultExceptionFactory != null ? defaultExceptionFactory : new DefaultConstraintExceptionFactory();
    }

    @Override
    public Throwable buildException(DatabaseConstraintViolation violation, Throwable cause) {
        checkNotNull(violation, "Cannot create exception for a null database constraint violation.");
        return getFirstSupportedFactory(violation).buildException(violation, cause);
    }

    /**
     * Retrieve the exception factory for a specific constraint. Whenever no
     * matching factory could we found, we return the default factory.
     * @param violation reference to our constraint violation
     * @return factory capable of building a violation exception for our constraint
     */
    private DatabaseConstraintExceptionFactory getFirstSupportedFactory(DatabaseConstraintViolation violation) {
        DatabaseConstraintExceptionFactory exceptionFactory = defaultExceptionFactory;
        for (ExceptionFactoryMapping exceptionFactoryMapping : exceptionFactoryMappings) {
            if (exceptionFactoryMapping.isSupported(violation)) {
                exceptionFactory = exceptionFactoryMapping.getExceptionFactory();
                break;
            }
        }
        return exceptionFactory;
    }

    /**
     * Register a custom exception for a constraint.
     * @param constraintName name of the constraint
     * @param matchingStrategy strategy used for matching
     * @param exceptionClass class of the exception (factory)
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(String constraintName, NameMatchingStrategy matchingStrategy, Class<?> exceptionClass) {
        return register(constraintName, matchingStrategy, new ReflectionConstraintExceptionFactory(exceptionClass));
    }
    
    /**
     * Register a custom exception factory for a constraint.
     * @param constraintName name of the constraint
     * @param matchingStrategy strategy used for matching
     * @param exceptionFactory creates the exception
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(String constraintName, NameMatchingStrategy matchingStrategy, DatabaseConstraintExceptionFactory exceptionFactory) {
        return register(new NameMatchingPredicate(constraintName, matchingStrategy), exceptionFactory);
    }

    /**
     * Register an exception factory for database constraints that match a certain criteria.
     * @param violationPredicate describes the criteria that our constraints must match
     * @param exceptionFactory the exception factory that should be used
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(Predicate<DatabaseConstraintViolation> violationPredicate, DatabaseConstraintExceptionFactory exceptionFactory) {
        exceptionFactoryMappings.add(new ExceptionFactoryMapping(violationPredicate, exceptionFactory));
        return this;
    }

    /**
     * Register all {@code NamedConstraint} annotated exception (factories) inside
     * the specified base package. Use this method to quickly register all exceptions.
     * @param basePackage the base package to search in
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory registerAll(String basePackage) {
        if (isNotBlank(basePackage)) {
	        Set<Class<?>> annotatedClasses = Classes.getAllWithAnnotation(basePackage, NamedConstraint.class);
	        for(Class<?> annotatedClass : annotatedClasses) {
	            NamedConstraint annotation = AnnotationUtils.findAnnotation(annotatedClass, NamedConstraint.class);
	            register(annotation.value(), annotation.strategy(), annotatedClass);
	        }
        }
        return this;
    }
    
    private static class ExceptionFactoryMapping {
        
        private final Predicate<DatabaseConstraintViolation> violationPredicate;
        
        private final DatabaseConstraintExceptionFactory exceptionFactory;

        public ExceptionFactoryMapping(Predicate<DatabaseConstraintViolation> violationPredicate, DatabaseConstraintExceptionFactory exceptionFactory) {
            this.violationPredicate = checkNotNull(violationPredicate, "Violation predicate cannot be null.");
            this.exceptionFactory = checkNotNull(exceptionFactory, "Exception factory cannot be null.");
        }

        public boolean isSupported(DatabaseConstraintViolation violation) {
            return violationPredicate.apply(violation);
        }

        public DatabaseConstraintExceptionFactory getExceptionFactory() {
            return exceptionFactory;
        }
        
    }

}
