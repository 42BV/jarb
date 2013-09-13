package org.jarbframework.constraint;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.HibernateJpaColumnMetadataRepositoryFactoryBean;
import org.jarbframework.utils.orm.hibernate.HibernateJpaSchemaMapper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Registers all required beans for constraint meta data management.
 * <br>This includes the following beans:
 * <ul>
 * <li>SchemaMapper</li>
 * <li>ColumnMetadataRepository</li>
 * <li>BeanConstraintDescriptor</li>
 * </ul>
 * 
 * @author Jeroen van Schagen
 */
public class EnableMetadataBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String schemaMapperName = buildAndRegisterSchemaMapper(element, parserContext);
        String columnRepositoryName = buildAndRegisterColumnRepository(element, parserContext);
        buildAndRegisterConstraintDescriptor(schemaMapperName, columnRepositoryName, parserContext);
        return null;
    }

    private String buildAndRegisterSchemaMapper(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder schemaMapperBuilder = BeanDefinitionBuilder.genericBeanDefinition(HibernateJpaSchemaMapper.class);
        schemaMapperBuilder.addConstructorArgReference(element.getAttribute("entity-manager-factory"));
        return parserContext.getReaderContext().registerWithGeneratedName(schemaMapperBuilder.getBeanDefinition());
    }

    private String buildAndRegisterColumnRepository(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder columnRepositoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(HibernateJpaColumnMetadataRepositoryFactoryBean.class);
        columnRepositoryBuilder.addPropertyReference("entityManagerFactory", element.getAttribute("entity-manager-factory"));
        columnRepositoryBuilder.addPropertyValue("caching", element.getAttribute("caching"));
        return parserContext.getReaderContext().registerWithGeneratedName(columnRepositoryBuilder.getBeanDefinition());
    }

    private void buildAndRegisterConstraintDescriptor(String schemaMapperName, String columnRepositoryName, ParserContext parserContext) {
        BeanDefinitionBuilder constraintDescriptorBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanConstraintDescriptorFactoryBean.class);
        constraintDescriptorBuilder.addPropertyReference("schemaMapper", schemaMapperName);
        constraintDescriptorBuilder.addPropertyReference("columnMetadataRepository", columnRepositoryName);
        parserContext.getReaderContext().registerWithGeneratedName(constraintDescriptorBuilder.getBeanDefinition());
    }

}
