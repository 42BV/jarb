package org.jarbframework.migrations.liquibase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import liquibase.resource.ResourceAccessor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.google.common.collect.Iterators;

/**
 * Resource accessor that adapts to our Spring {@link ResourceLoader}. This resource
 * accessor allows us to, e.g. use "classpath:" and "file:" prefixes.
 * @author Jeroen van Schagen
 */
public class SpringResourceAccessor implements ResourceAccessor, ResourceLoaderAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private ResourceLoader resourceLoader;

    private String basePath;
    
    @Override
    public InputStream getResourceAsStream(String file) throws IOException {
        return getResource(file).getInputStream();
    }
    
    private Resource getResource(String relativePath) {
        String fullPath = getFullPath(relativePath);
        logger.debug("Loading resource: {}", fullPath);
        return resourceLoader.getResource(fullPath);
    }
    
    private String getFullPath(String relativePath) {
        if (StringUtils.isNotBlank(basePath)) {
            return basePath + File.separator + relativePath;
        } else {
            return relativePath;
        }
    }
    
    @Override
    public Enumeration<URL> getResources(String packageName) throws IOException {
        Resource resource = getResource(packageName);
        return getChildURLs(resource.getFile());
    }
    
    private Enumeration<URL> getChildURLs(File parent) throws MalformedURLException {
        File[] files = parent.listFiles();
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        return Iterators.asEnumeration(Iterators.forArray(urls));
    }
    
    @Override
    public ClassLoader toClassLoader() {
        return resourceLoader.getClassLoader();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
}
