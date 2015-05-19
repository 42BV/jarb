package org.jarbframework.sample;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Creates a dispatcher servlet on "/", using our configured Spring mappings.
 * This is the entry point of our application, from the Tomcat servlet container.
 * During startup this initializer is automatically detected by Spring, because
 * it implements the {@code WebApplicationInitializer} interface.
 *
 * @author Jeroen van Schagen
 */
public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { ApplicationConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebMvcConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

}
