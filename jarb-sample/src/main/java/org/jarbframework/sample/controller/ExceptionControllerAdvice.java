package org.jarbframework.sample.controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Advice for handling exceptions in controllers.
 *
 * @author Jeroen van Schagen
 * @since Mar 27, 2015
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    
    private static final String FIELDS_NAME = "fields";
    private static final String ERROR_NAME = "error";

    /**
     * Handles the exception when an entity has field errors during validation.
     * 
     * @param response the response
     * @param ex the exception
     * @return the field errors as JSON
     */
    @ExceptionHandler({ ConstraintViolationException.class })
    public ModelAndView handleFieldErrors(HttpServletResponse response, ConstraintViolationException ex) {
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        List<FieldErrorModel> fields = new ArrayList<FieldErrorModel>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            fields.add(new FieldErrorModel(violation));
        }
        return new ModelAndView(new MappingJackson2JsonView(), FIELDS_NAME, fields);
    }

    /**
     * Handles all other exceptions. Causing the exception to be logged and returns a description.
     * 
     * @param response the response
     * @param handler the handler method
     * @param ex the exception
     * @return the result JSON
     */
    @ExceptionHandler({ Exception.class })
    public ModelAndView handleOther(HttpServletResponse response, Object handler, Exception ex) {
        LOGGER.error("Handling request, for [" + handler + "], resulted in the following exception.", ex);
        response.setStatus(INTERNAL_SERVER_ERROR.value());
        return new ModelAndView(new MappingJackson2JsonView(), ERROR_NAME, new ExceptionModel(ex));
    }

    /**
     * Field level error.
     *
     * @author Jeroen van Schagen
     * @since Mar 27, 2015
     */
    public static final class FieldErrorModel {

        private final Class<?> rootBeanClass;

        private final String propertyPath;
        
        private final Object invalidValue;
        
        private final String message;

        private FieldErrorModel(ConstraintViolation<?> violation) {
            this.rootBeanClass = violation.getRootBeanClass();
            this.propertyPath = violation.getPropertyPath().toString();
            this.invalidValue = violation.getInvalidValue();
            this.message = violation.getMessage();
        }

        public Class<?> getRootBeanClass() {
            return rootBeanClass;
        }
        
        public String getPropertyPath() {
            return propertyPath;
        }
        
        public Object getInvalidValue() {
            return invalidValue;
        }

        public String getMessage() {
            return message;
        }

    }
    
    /**
     * Describes an exception.
     *
     * @author Jeroen van Schagen
     * @since Mar 27, 2015
     */
    public static final class ExceptionModel {
        
        private final Class<?> type;
        
        private final String message;

        private ExceptionModel(Exception ex) {
            this.type = ex.getClass();
            this.message = ex.getMessage();
        }
        
        public Class<?> getType() {
            return type;
        }
        
        public String getMessage() {
            return message;
        }

    }

}
