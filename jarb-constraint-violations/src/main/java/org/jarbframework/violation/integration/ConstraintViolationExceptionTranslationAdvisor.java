package org.jarbframework.violation.integration;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jarbframework.violation.DatabaseConstraintExceptionTranslator;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

/**
 * Provides aspect oriented advise on how to perform constraint violation exception translations.
 * 
 * @author Jeroen van Schagen
 * @since 19-05-2011
 */
public class ConstraintViolationExceptionTranslationAdvisor extends AbstractPointcutAdvisor {
    private static final long serialVersionUID = -462142297188191739L;

    /** Describes how exceptions should be translated **/
    private final DatabaseConstraintExceptionTranslator exceptionTranslator;
    /** Describes when the translation process should be triggered **/
    private final Pointcut pointcut;

    /**
     * Construct a new {@link ConstraintViolationExceptionTranslationAdvisor}.
     * 
     * @param exceptionTranslator translates the exceptions into constraint violation exceptions
     * @param pointcut aspect oriented pointcut, describes which methods should be intercepted
     */
    public ConstraintViolationExceptionTranslationAdvisor(DatabaseConstraintExceptionTranslator exceptionTranslator, Pointcut pointcut) {
        this.pointcut = pointcut;
        this.exceptionTranslator = exceptionTranslator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Advice getAdvice() {
        return new ExceptionTranslatingMethodInterceptor();
    }

    /**
     * Intercepts method invocations, and potentially translates the runtime exceptions into
     * constraint violation exceptions.
     */
    private class ExceptionTranslatingMethodInterceptor implements MethodInterceptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            try {
                return invocation.proceed();
            } catch (RuntimeException exception) {
                Throwable translatedException = exceptionTranslator.translateExceptionIfPossible(exception);
                if (translatedException != null) {
                    // If a translation could be made, throw the translated exception instead
                    throw translatedException;
                } else {
                    // Otherwise just throw the origional runtime exception
                    throw exception;
                }
            }
        }

    }

}
