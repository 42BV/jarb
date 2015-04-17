package org.jarbframework.sample;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ModelAndView handleFieldErrors(HttpServletResponse response, MethodArgumentNotValidException ex) {
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        List<FieldErrorModel> fields = new ArrayList<FieldErrorModel>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.add(new FieldErrorModel(error));
        }
        return new ModelAndView(new MappingJackson2JsonView(), FIELDS_NAME, fields);
    }

    /**
     * Handles the exception when a request method is not supported.
     * 
     * @param response the response
     * @return an empty response in JSON
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public ModelAndView handleRequestMethodNotSupported(HttpServletResponse response, HttpRequestMethodNotSupportedException ex) {
        response.setStatus(NOT_FOUND.value());
        return new ModelAndView(new MappingJackson2JsonView(), ERROR_NAME, new ExceptionModel(ex));
    }

    /**
     * Handles the exception when a required parameter is missing.
     * 
     * @param response the response
     * @return the result JSON
     */
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ModelAndView handleBadRequest(HttpServletResponse response, MissingServletRequestParameterException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ModelAndView(new MappingJackson2JsonView(), ERROR_NAME, new ExceptionModel(ex));
    }

    /**
     * Handles all other exceptions. Causing the exception to be logged and returns a description.
     * 
     * @param request the request
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

        private final String field;

        private final String code;

        private final String message;

        private FieldErrorModel(FieldError error) {
            this.field = error.getField();
            this.code = error.getCode();
            this.message = error.getDefaultMessage();
        }

        public String getField() {
            return field;
        }

        public String getCode() {
            return code;
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
