/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample.config;

import java.text.SimpleDateFormat;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "org.jarbframework.sample", 
               includeFilters = { @Filter(Controller.class)},
               excludeFilters = { @Filter(Service.class), @Filter(Repository.class), @Filter(Configuration.class) })
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/styles/**").addResourceLocations("/styles/");
        registry.addResourceHandler("/scripts/**").addResourceLocations("/scripts/");
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJacksonHttpMessageConverter());
    }
    
    private MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }
    
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("MM-dd-yyyy"));
        return objectMapper;
    }
    
    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

}