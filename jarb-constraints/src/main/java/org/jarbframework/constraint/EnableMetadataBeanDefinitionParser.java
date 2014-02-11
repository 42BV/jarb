package org.jarbframework.constraint;

import org.jarbframework.constraint.metadata.BeanConstraintDescriptorFactoryBean;
import org.jarbframework.constraint.metadata.database.HibernateJpaBeanMetadataRepositoryFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Registers all required beans for constraint meta data management.
 * <br>This includes the following beans:
 * <ul>
 *  <li>BeanMetadataRepository</li>
 *  <li>BeanConstraintDescriptor</li>
 * </ul>
 * 
 * @author Jeroen van Schagen
 */
public class EnableMetadataBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String beanMetadataRepositoryName = buildAndRegisterBeanMetadataRepository(element, parserContext);
        buildAndRegisterConstraintDescriptor(beanMetadataRepositoryName, parserContext);
        return null;
    }

    private String buildAndRegisterBeanMetadataRepository(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder beanMetadataRepositoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(HibernateJpaBeanMetadataRepositoryFactoryBean.class);
        beanMetadataRepositoryBuilder.addConstructorArgReference(element.getAttribute("entity-manager-factory"));
        return parserContext.getReaderContext().registerWithGeneratedName(beanMetadataRepositoryBuilder.getBeanDefinition());
    }

    private void buildAndRegisterConstraintDescriptor(String beanMetadataRepositoryName, ParserContext parserContext) {
        BeanDefinitionBuilder beanConstraintDescriptorBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanConstraintDescriptorFactoryBean.class);
        beanConstraintDescriptorBuilder.addConstructorArgReference(beanMetadataRepositoryName);
        parserContext.getReaderContext().registerWithGeneratedName(beanConstraintDescriptorBuilder.getBeanDefinition());
    }

}
