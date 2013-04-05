package org.jarbframework.constraint.xml;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.HibernateJpaColumnMetadataRepositoryFactoryBean;
import org.jarbframework.utils.orm.SimpleHibernateJpaSchemaMapper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Registers all required beans for constraint meta-data retrieval.
 * @author Jeroen van Schagen
 */
public class EnableMetadataBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String schemaMapperName = registerSchemaMapper(element, parserContext);
        String columnRepositoryName = registerColumnRepository(element, parserContext);
        
        BeanDefinition descriptor = buildConstraintDescriptor(schemaMapperName, columnRepositoryName);
        registerWithGeneratedName(parserContext, descriptor);
        
        return null;
    }

    private String registerSchemaMapper(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder schemaMapperBuilder = BeanDefinitionBuilder.genericBeanDefinition(SimpleHibernateJpaSchemaMapper.class);
        schemaMapperBuilder.addConstructorArgReference(element.getAttribute("entity-manager-factory"));
        return registerWithGeneratedName(parserContext, schemaMapperBuilder.getBeanDefinition());
    }
    
    private String registerColumnRepository(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder columnRepositoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(HibernateJpaColumnMetadataRepositoryFactoryBean.class);
        columnRepositoryBuilder.addPropertyReference("entityManagerFactory", element.getAttribute("entity-manager-factory"));
        columnRepositoryBuilder.addPropertyValue("caching", element.getAttribute("caching"));
        return registerWithGeneratedName(parserContext, columnRepositoryBuilder.getBeanDefinition());
    }

    private String registerWithGeneratedName(ParserContext parserContext, BeanDefinition beanDefintion) {
        return parserContext.getReaderContext().registerWithGeneratedName(beanDefintion);
    }

    private BeanDefinition buildConstraintDescriptor(String schemaMapperName, String columnRepositoryName) {
        BeanDefinitionBuilder constraintDescriptorBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanConstraintDescriptorFactoryBean.class);
        constraintDescriptorBuilder.addPropertyReference("schemaMapper", schemaMapperName);
        constraintDescriptorBuilder.addPropertyReference("columnMetadataRepository", columnRepositoryName);
        return constraintDescriptorBuilder.getBeanDefinition();
    }
    
}
