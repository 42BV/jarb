package nl._42.jarb.constraint.violation;

import java.lang.annotation.Annotation;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.stereotype.Repository;

/**
 * Wraps beans with exception translation logic. Whenever possible, runtime exceptions, thrown
 * by the repository bean, are translated into (more obvious) constraint violation exceptions.
 * By using this bean post processor our database exceptions become easier to read, and can be
 * caught programmatically. Also, it is possible to access violation information directly
 * from the exception API.
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
public class TranslateAdviceAddingBeanPostProcessor extends AdvisorAddingBeanPostProcessor {

    /** Converted into a persistence exception translator. */
    private final DatabaseConstraintExceptionTranslator translator;

    /** Indicates where exception translation should be plugged into. */
    private final Pointcut pointcut;

    /**
     * Create a new translate exception bean post processor on @Repository beans.
     * 
     * @param translator the exception translator
     */
    public TranslateAdviceAddingBeanPostProcessor(DatabaseConstraintExceptionTranslator translator) {
        this(translator, Repository.class);
    }
    
    /**
     * Create a new translate exception bean post processor on specific annotated beans.
     * 
     * @param translator the exception translator
     * @param annotationClass the annotation that should bind our translations
     */
    public TranslateAdviceAddingBeanPostProcessor(DatabaseConstraintExceptionTranslator translator, Class<? extends Annotation> annotationClass) {
        this(translator, new AnnotationMatchingPointcut(annotationClass, true));
    }

    /**
     * Create a new translate exception bean post processors.
     * 
     * @param translator the exception translator
     * @param pointcut the pointcut
     */
    public TranslateAdviceAddingBeanPostProcessor(DatabaseConstraintExceptionTranslator translator, Pointcut pointcut) {
        this.translator = translator;
        this.pointcut = pointcut;

        setAdvisor(new ExceptionTranslatingAdvisor());
        setAddUpFront(true);
    }

    private class ExceptionTranslatingAdvisor extends AbstractPointcutAdvisor {

        @Override
        public Pointcut getPointcut() {
            return pointcut;
        }

        @Override
        public Advice getAdvice() {
            return new MethodInterceptor() {

                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    try {
                        return invocation.proceed();
                    } catch (RuntimeException exception) {
                        throw translate(exception);
                    }
                }
                
                /**
                 * Attempt to translate the exception.
                 * 
                 * @param exception the original exception
                 * @return the original, or translated, exception
                 */
                private Throwable translate(RuntimeException exception) {
                    Throwable translation = translator.translate(exception);
                    return translation != null ? translation : exception;
                }

            };
        }
        
    }
    
}
