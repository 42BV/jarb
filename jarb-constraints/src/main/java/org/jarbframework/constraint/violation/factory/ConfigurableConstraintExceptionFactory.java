package org.jarbframework.constraint.violation.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.mapping.DatabaseConstraint;
import org.jarbframework.constraint.violation.factory.mapping.ExceptionFactoryMapping;
import org.jarbframework.constraint.violation.factory.mapping.NameMatchingStrategy;
import org.jarbframework.constraint.violation.factory.mapping.ViolationNamePredicate;
import org.jarbframework.utils.Classes;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

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
    private final List<ExceptionFactoryMapping> customFactoryMappings = new ArrayList<ExceptionFactoryMapping>();
    
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
        for (ExceptionFactoryMapping customFactoryMapping : customFactoryMappings) {
            if (customFactoryMapping.isSupported(violation)) {
                resultFactory = customFactoryMapping.getExceptionFactory();
                break;
            }
        }
        return resultFactory;
    }

    public ConfigurableConstraintExceptionFactory register(String constraintName, Class<? extends Exception> exceptionClass) {
        return register(constraintName, NameMatchingStrategy.EXACT_IGNORE_CASE, exceptionClass);
    }
    
    public ConfigurableConstraintExceptionFactory register(String constraintName, NameMatchingStrategy nameMatchingStrategy, Class<? extends Exception> exceptionClass) {
        return register(new ViolationNamePredicate(constraintName, nameMatchingStrategy), exceptionClass);
    }
    
    /**
     * Register an exception for database constraints that match a certain criteria.
     * @param violationPredicate describes the criteria that our constraints must match
     * @param exceptionClass the exception that should be created
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory register(Predicate<DatabaseConstraintViolation> violationPredicate, Class<? extends Exception> exceptionClass) {
        return register(violationPredicate, new ReflectionConstraintExceptionFactory(exceptionClass));
    }
    
    /**
     * Register an exception factory for database constraints that match a certain criteria.
     * @param violationPredicate describes the criteria that our constraints must match
     * @param exceptionFactory the exception factory that should be used
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory register(Predicate<DatabaseConstraintViolation> violationPredicate, DatabaseConstraintExceptionFactory exceptionFactory) {
        return register(new ExceptionFactoryMapping(violationPredicate, exceptionFactory));
    }

    /**
     * Register an exception factory for database constraints that match a certain criteria.
     * @param exceptionFactoryMapping describes the mapping of an exception factory on constraint violations.
     * @return this factory instance for chaining
     */
    public ConfigurableConstraintExceptionFactory register(ExceptionFactoryMapping exceptionFactoryMapping) {
        customFactoryMappings.add(exceptionFactoryMapping);
        return this;
    }
    
    public ConfigurableConstraintExceptionFactory registerAll(String basePackage) {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
        componentProvider.addIncludeFilter(new AnnotationTypeFilter(DatabaseConstraint.class));
        
        Set<BeanDefinition> annotatedBeans = componentProvider.findCandidateComponents(basePackage);
        for(BeanDefinition annotatedBean : annotatedBeans) {
            Class<?> beanClass = Classes.forName(annotatedBean.getBeanClassName());
            DatabaseConstraint annotation = AnnotationUtils.findAnnotation(beanClass, DatabaseConstraint.class);
            register(annotation.value(), annotation.strategy(), beanClass.asSubclass(Exception.class));
        }
        
        return this;
    }

}
