package nl._42.jarb.constraint.violation.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.utils.Asserts;
import nl._42.jarb.utils.ClassScanner;
import nl._42.jarb.utils.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

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
        this(new DefaultConstraintExceptionFactory());
    }

    public ConfigurableConstraintExceptionFactory(DatabaseConstraintExceptionFactory defaultExceptionFactory) {
        this.defaultExceptionFactory = defaultExceptionFactory;
    }

    @Override
    public Throwable buildException(DatabaseConstraintViolation violation, Throwable cause) {
        Asserts.notNull(violation, "Cannot create exception for a null database constraint violation.");
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
     * @param constraintNames name of the constraint
     * @param matchingStrategy strategy used for matching
     * @param exceptionClass class of the exception (factory)
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(String[] constraintNames, NameMatchingStrategy matchingStrategy, Class<?> exceptionClass) {
        return register(constraintNames, matchingStrategy, new ReflectionConstraintExceptionFactory(exceptionClass));
    }
    
    /**
     * Register a custom exception factory for a constraint.
     * @param constraintNames name of the constraint
     * @param matchingStrategy strategy used for matching
     * @param exceptionFactory creates the exception
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(String[] constraintNames, NameMatchingStrategy matchingStrategy, DatabaseConstraintExceptionFactory exceptionFactory) {
        return register(new NameMatchingPredicate(constraintNames, matchingStrategy), exceptionFactory);
    }

    /**
     * Register an exception factory for database constraints that match a certain criteria.
     * @param predicate describes the criteria that our constraints must match
     * @param exceptionFactory the exception factory that should be used
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory register(ViolationPredicate predicate, DatabaseConstraintExceptionFactory exceptionFactory) {
        exceptionFactoryMappings.add(new ExceptionFactoryMapping(predicate, exceptionFactory));
        return this;
    }

    /**
     * Register all {@code NamedConstraint} annotated exception (factories) inside
     * the specified base package. Use this method to quickly register all exceptions.
     * @param basePackage the base package to search in
     * @return this factory instance, for chaining
     */
    public ConfigurableConstraintExceptionFactory registerAll(String basePackage) {
        if (StringUtils.isNotBlank(basePackage)) {
            Set<Class<?>> annotatedClasses = ClassScanner.getAllWithAnnotation(basePackage, NamedConstraint.class);
	        for(Class<?> annotatedClass : annotatedClasses) {
	            NamedConstraint namedConstraint = AnnotationUtils.findAnnotation(annotatedClass, NamedConstraint.class);
	            register(namedConstraint.value(), namedConstraint.strategy(), annotatedClass);
	        }
        }

        return this;
    }
    
    private static class ExceptionFactoryMapping {
        
        private final ViolationPredicate predicate;
        
        private final DatabaseConstraintExceptionFactory exceptionFactory;

        public ExceptionFactoryMapping(ViolationPredicate predicate, DatabaseConstraintExceptionFactory exceptionFactory) {
            this.predicate = Asserts.notNull(predicate, "Violation predicate cannot be null.");
            this.exceptionFactory = Asserts.notNull(exceptionFactory, "Exception factory cannot be null.");
        }

        public boolean isSupported(DatabaseConstraintViolation violation) {
            return predicate.isSupported(violation);
        }

        public DatabaseConstraintExceptionFactory getExceptionFactory() {
            return exceptionFactory;
        }
        
    }

}
