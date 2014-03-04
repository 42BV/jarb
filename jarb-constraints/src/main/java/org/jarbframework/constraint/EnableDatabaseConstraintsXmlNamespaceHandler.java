package org.jarbframework.constraint;

import org.jarbframework.constraint.metadata.DefaultBeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepositoryFactoryBean;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatorFactoryBean;
import org.jarbframework.constraint.violation.ExceptionTranslatingBeanPostProcessor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * XML namespace handler for database constraint handling.
 *
 * @author Jeroen van Schagen
 * @since Feb 11, 2014
 */
public class EnableDatabaseConstraintsXmlNamespaceHandler extends NamespaceHandlerSupport {
    
    // Attribute names

    private static final String POINTCUT = "pointcut";
    private static final String DATA_SOURCE = "data-source";
    private static final String BASE_PACKAGE = "base-package";
    private static final String ENTITY_MANAGER_FACTORY = "entity-manager-factory";
    private static final String DEFAULT_EXCEPTION_FACTORY = "default-exception-factory";

    @Override
    public void init() {
        registerBeanDefinitionParser("enable-constraints", new EnableConstraintBeanDefinitionParser());
    }
    
    private static class EnableConstraintBeanDefinitionParser implements BeanDefinitionParser {
        
        @Override
        public BeanDefinition parse(Element element, ParserContext parserContext) {
            // Meta-data descriptors
            String beanMetadataRepositoryName = buildAndRegisterBeanMetadataRepository(element, parserContext);
            buildAndRegisterConstraintDescriptor(beanMetadataRepositoryName, element, parserContext);
            
            // Exception translation
            buildAndRegisterExceptionTranslatingPostProcessor(element, parserContext);

            return null;
        }
        
        private String buildAndRegisterBeanMetadataRepository(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder beanMetadataRepositoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanMetadataRepositoryFactoryBean.class);
            if (element.hasAttribute(ENTITY_MANAGER_FACTORY)) {
                beanMetadataRepositoryBuilder.addConstructorArgReference(element.getAttribute(ENTITY_MANAGER_FACTORY));
            } else {
                beanMetadataRepositoryBuilder.addConstructorArgReference(element.getAttribute(DATA_SOURCE));
            }
            return parserContext.getReaderContext().registerWithGeneratedName(beanMetadataRepositoryBuilder.getBeanDefinition());
        }
        
        private void buildAndRegisterConstraintDescriptor(String beanMetadataRepositoryName, Element element, ParserContext parserContext) {
            BeanDefinitionBuilder beanConstraintDescriptorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultBeanConstraintDescriptor.class);
            beanConstraintDescriptorBuilder.addConstructorArgReference(beanMetadataRepositoryName);
            parserContext.getReaderContext().registerWithGeneratedName(beanConstraintDescriptorBuilder.getBeanDefinition());
        }
        
        private void buildAndRegisterExceptionTranslatingPostProcessor(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder processorBuilder = BeanDefinitionBuilder.genericBeanDefinition(ExceptionTranslatingBeanPostProcessor.class);
            processorBuilder.addConstructorArgValue(buildExceptionTranslator(element));
            if (element.hasAttribute("pointcut")) {
                processorBuilder.addConstructorArgValue(buildPointcut(element.getAttribute(POINTCUT)));
            }
            parserContext.getReaderContext().registerWithGeneratedName(processorBuilder.getBeanDefinition());
        }
        
        private BeanDefinition buildExceptionTranslator(Element element) {
            BeanDefinitionBuilder translatorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslatorFactoryBean.class);
            translatorBuilder.addPropertyValue("basePackage", element.getAttribute(BASE_PACKAGE));
            if (element.hasAttribute(ENTITY_MANAGER_FACTORY)) {
                translatorBuilder.addConstructorArgReference(element.getAttribute(ENTITY_MANAGER_FACTORY));
            } else {
                translatorBuilder.addConstructorArgReference(element.getAttribute(DATA_SOURCE));
            }
            if (element.hasAttribute(DEFAULT_EXCEPTION_FACTORY)) {
                translatorBuilder.addPropertyReference("defaultExceptionFactory", element.getAttribute(DEFAULT_EXCEPTION_FACTORY));
            }
            return translatorBuilder.getBeanDefinition();
        }
        
        private BeanDefinition buildPointcut(String expression) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(AspectJExpressionPointcut.class);
            beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            beanDefinition.setSynthetic(true);
            beanDefinition.getPropertyValues().add("expression", expression);
            return beanDefinition;
        }
        
    }

}
