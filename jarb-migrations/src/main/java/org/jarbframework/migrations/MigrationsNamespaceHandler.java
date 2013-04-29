package org.jarbframework.migrations;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class MigrationsNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("migrate", new MigrateBeanDefinitionParser());
    }

    public static class MigrateBeanDefinitionParser implements BeanDefinitionParser {

        @Override
        public BeanDefinition parse(Element element, ParserContext parserContext) {
            final BeanDefinitionRegistry beanRegistry = parserContext.getRegistry();

            String dataSourceId = element.getAttribute("data-source");
            BeanDefinition dataSourceDefinition = beanRegistry.getBeanDefinition(dataSourceId);
            
            BeanDefinitionBuilder migrationDataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(MigratingDataSource.class);
            migrationDataSourceBuilder.addPropertyValue("delegate", dataSourceDefinition);
            migrationDataSourceBuilder.addPropertyReference("migrator", element.getAttribute("migrator"));
            
            // Overwrite the bean definition with our migrating data source
            beanRegistry.registerBeanDefinition(dataSourceId, migrationDataSourceBuilder.getBeanDefinition());
            return null;
        }

    }

}
