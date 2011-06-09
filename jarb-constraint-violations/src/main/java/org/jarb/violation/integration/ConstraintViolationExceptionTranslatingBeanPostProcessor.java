package org.jarb.violation.integration;

import org.jarb.utils.spring.AdvisorAddingBeanPostProcessor;
import org.jarb.violation.ConstraintViolationExceptionTranslator;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Wraps @Repository annotated beans with exception translation logic. Whenever possible,
 * runtime exceptions, thrown by the repository bean, are translated into (more obvious)
 * constraint violation exceptions. By using this bean post processor our database exceptions
 * become easier to read, and can be caught programmatically. Also, it is possible to access
 * violation information directly from the exception API.
 * 
 * For example:
 * 
 * <pre>
 * try {
 *   personRepository.add(new Person("henk de boer"));
 * } catch(UniqueKeyViolationException e) {
 *   if(e.getViolation().getColumnName().equals("name")) {
 *     error("Person name already exists");
 *   }
 * }
 * </pre>
 * 
 * It is also possible to register custom exceptions for a specific named constraint.
 * For example, we could say that the unique key "uk_persons_name" violations always
 * result in a PersonNameAlreadyExistsException. Enabling us to do the following:
 * 
 * <pre>
 * try {
 *   personRepository.add(new Person("henk de boer"));
 * } catch(PersonNameAlreadyExistsException e) {
 *   error("Person " + e.getViolation().getValue() + " already exists");
 * }
 * </pre>
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public class ConstraintViolationExceptionTranslatingBeanPostProcessor extends AdvisorAddingBeanPostProcessor implements InitializingBean {
    private static final long serialVersionUID = -292839286392224628L;

    /** Indicates where exception translation should be plugged into. */
    private Pointcut pointcut = new AnnotationMatchingPointcut(Repository.class, true);
    /** Converted into a persistence exception translator. */
    private ConstraintViolationExceptionTranslator translator;

    /** Describes the translation logic that should be performed. */
    private Advisor translationAdvisor;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Advisor getAdvisor() {
        return translationAdvisor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(translator != null, "Exception translator cannot be null");
        Assert.state(pointcut != null, "Pointcut cannot be null");
        translationAdvisor = new ConstraintViolationExceptionTranslationAdvisor(translator, pointcut);
    }

    /**
     * Configure where translation logic should be applied.
     * @param pointcut describes where the translation should be applied
     */
    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    /**
     * Configure the exception translator instance. Exceptions will be translated with this instance.
     * @param translator the translator that will translate our instances
     */
    public void setTranslator(ConstraintViolationExceptionTranslator translator) {
        this.translator = translator;
    }

}