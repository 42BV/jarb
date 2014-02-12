package org.jarbframework.constraint;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.HibernateJpaBeanMetadataRepositoryFactoryBean;
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

    @Override
    public void init() {
        registerBeanDefinitionParser("enable-constraints", new EnableConstraintBeanDefinitionParser());
    }
    
    private static class EnableConstraintBeanDefinitionParser implements BeanDefinitionParser {
        
        @Override
        public BeanDefinition parse(Element element, ParserContext parserContext) {
            if (element.hasAttribute("entity-manager-factory")) {
                String beanMetadataRepositoryName = buildAndRegisterBeanMetadataRepository(element, parserContext);
                buildAndRegisterConstraintDescriptor(beanMetadataRepositoryName, element, parserContext);
            }

            buildAndRegisterExceptionTranslatingPostProcessor(element, parserContext);
            return null;
        }
        
        private String buildAndRegisterBeanMetadataRepository(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder beanMetadataRepositoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(HibernateJpaBeanMetadataRepositoryFactoryBean.class);
            beanMetadataRepositoryBuilder.addConstructorArgReference(element.getAttribute("entity-manager-factory"));
            return parserContext.getReaderContext().registerWithGeneratedName(beanMetadataRepositoryBuilder.getBeanDefinition());
        }
        
        private void buildAndRegisterConstraintDescriptor(String beanMetadataRepositoryName, Element element, ParserContext parserContext) {
            BeanDefinitionBuilder beanConstraintDescriptorBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanConstraintDescriptorFactoryBean.class);
            beanConstraintDescriptorBuilder.addConstructorArgReference(beanMetadataRepositoryName);
            parserContext.getReaderContext().registerWithGeneratedName(beanConstraintDescriptorBuilder.getBeanDefinition());
        }
        
        private void buildAndRegisterExceptionTranslatingPostProcessor(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder processorBuilder = BeanDefinitionBuilder.genericBeanDefinition(ExceptionTranslatingBeanPostProcessor.class);
            processorBuilder.addConstructorArgValue(buildExceptionTranslator(element));
            if (element.hasAttribute("pointcut")) {
                processorBuilder.addConstructorArgValue(buildPointcut(element.getAttribute("pointcut")));
            }
            parserContext.getReaderContext().registerWithGeneratedName(processorBuilder.getBeanDefinition());
        }
        
        private BeanDefinition buildExceptionTranslator(Element element) {
            BeanDefinitionBuilder translatorBuilder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslatorFactoryBean.class);
            translatorBuilder.addPropertyValue("basePackage", element.getAttribute("base-package"));
            if (element.hasAttribute("entity-manager-factory")) {
                translatorBuilder.addPropertyReference("entityManagerFactory", element.getAttribute("entity-manager-factory"));
            } else {
                translatorBuilder.addPropertyReference("dataSource", element.getAttribute("data-source"));
            }
            if (element.hasAttribute("default-exception-factory")) {
                translatorBuilder.addPropertyReference("defaultExceptionFactory", element.getAttribute("default-exception-factory"));
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
