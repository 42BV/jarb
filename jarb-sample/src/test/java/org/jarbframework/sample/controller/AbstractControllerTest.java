/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample.controller;

import org.jarbframework.sample.WebMvcConfig;
import org.jarbframework.utils.Asserts;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bas de Vos
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractControllerTest {

    private final WebMvcConfig webConfig = new WebMvcConfig();

    protected MockMvc webClient;

    /**
     * Use this method in a subclass in a @Before annotated method to initialize the MockMvc webClient for the controller under test.
     * With MockMvc it is possible to issue get- and post requests instead of invoking a controller method directly.
     * 
     * More information on the usage of the MockMvc API:
     * http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/testing.html
     * 
     * @param controller - the controller under test.
     */
    protected void initWebClient(Object controller) {
        Asserts.state(isController(controller), "Should be an annotated controller.");

        this.webClient = MockMvcBuilders.standaloneSetup(controller)
                            .setMessageConverters(webConfig.mappingJacksonHttpMessageConverter())
                            .setConversionService(new FormattingConversionService())
                                .build();
    }
    
    private boolean isController(Object controller) {
        Class<? extends Object> controllerClass = controller.getClass();
        return controllerClass.getAnnotation(Controller.class) != null || controllerClass.getAnnotation(RestController.class) != null;
    }

}
