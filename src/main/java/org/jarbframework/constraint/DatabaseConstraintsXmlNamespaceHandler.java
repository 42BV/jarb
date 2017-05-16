package org.jarbframework.constraint;

import org.jarbframework.constraint.metadata.DefaultBeanConstraintDescriptor;
import org.jarbframework.constraint.metadata.database.BeanMetadataRepositoryFactoryBean;
import org.jarbframework.constraint.violation.DatabaseConstraintExceptionTranslatorFactoryBean;
import org.jarbframework.constraint.violation.TranslateAdviceAddingBeanPostProcessor;
import org.jarbframework.utils.Classes;
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
public class DatabaseConstraintsXmlNamespaceHandler extends NamespaceHandlerSupport {
    
    // Attribute names

    private static final String POINTCUT = "pointcut";
    private static final String DATA_SOURCE = "data-source";
    private static final String BASE_PACKAGE = "base-package";
    private static final String ENTITY_MANAGER_FACTORY = "entity-manager-factory";
    private static final String DEFAULT_EXCEPTION_FACTORY = "default-exception-factory";
    
    // Beans references
    
    private static final String ENTITY_MANAGER_FACTORY_REF = "entityManagerFactory";
    private static final String DATA_SOURCE_REF = "dataSource";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        registerBeanDefinitionParser("enable-constraints", new EnableConstraintBeanDefinitionParser());
    }
    
    private static class EnableConstraintBeanDefinitionParser implements BeanDefinitionParser {
        
        /**
         * {@inheritDoc}
         */
        @Override
        public BeanDefinition parse(Element element, ParserContext parserContext) {
            String beanMetadataRepositoryName = buildAndRegisterBeanMetadataRepository(element, parserContext);
            buildAndRegisterConstraintDescriptor(beanMetadataRepositoryName, element, parserContext);
            buildAndRegisterExceptionTranslatingPostProcessor(element, parserContext);
            return null;
        }
        
        private String buildAndRegisterBeanMetadataRepository(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(BeanMetadataRepositoryFactoryBean.class);
            builder.addConstructorArgReference(getDataSourceFactoryReference(element));
            return parserContext.getReaderContext().registerWithGeneratedName(builder.getBeanDefinition());
        }
        
        private String getDataSourceFactoryReference(Element element) {
            if (element.hasAttribute(ENTITY_MANAGER_FACTORY)) {
                return element.getAttribute(ENTITY_MANAGER_FACTORY);
            } else if (element.hasAttribute(DATA_SOURCE)) {
                return element.getAttribute(DATA_SOURCE);
            } else {
                return Classes.hasClass("javax.persistence.EntityManager") ? ENTITY_MANAGER_FACTORY_REF : DATA_SOURCE_REF;
            }
        }

        private void buildAndRegisterConstraintDescriptor(String beanMetadataRepositoryName, Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DefaultBeanConstraintDescriptor.class);
            builder.addConstructorArgReference(beanMetadataRepositoryName);
            parserContext.getReaderContext().registerWithGeneratedName(builder.getBeanDefinition());
        }
        
        private void buildAndRegisterExceptionTranslatingPostProcessor(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(TranslateAdviceAddingBeanPostProcessor.class);
            builder.addConstructorArgValue(buildExceptionTranslator(element));
            if (element.hasAttribute("pointcut")) {
                builder.addConstructorArgValue(buildPointcut(element.getAttribute(POINTCUT)));
            }
            parserContext.getReaderContext().registerWithGeneratedName(builder.getBeanDefinition());
        }
        
        private BeanDefinition buildExceptionTranslator(Element element) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseConstraintExceptionTranslatorFactoryBean.class);
            builder.addPropertyValue("basePackage", element.getAttribute(BASE_PACKAGE));
            builder.addConstructorArgReference(getDataSourceFactoryReference(element));
            if (element.hasAttribute(DEFAULT_EXCEPTION_FACTORY)) {
                builder.addPropertyReference("defaultExceptionFactory", element.getAttribute(DEFAULT_EXCEPTION_FACTORY));
            }
            return builder.getBeanDefinition();
        }
        
        private BeanDefinition buildPointcut(String expression) {
            RootBeanDefinition bean = new RootBeanDefinition(AspectJExpressionPointcut.class);
            bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            bean.setSynthetic(true);
            bean.getPropertyValues().add("expression", expression);
            return bean;
        }
        
    }

}
