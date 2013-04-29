package org.jarbframework.migrations;

import org.jarbframework.migrations.liquibase.LiquibaseMigrator;
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
            final String dataSourceId = element.getAttribute("data-source");
            BeanDefinitionRegistry beanRegistry = parserContext.getRegistry();
            
            // Overwrite our existing data source definition with the migrating data source
            BeanDefinition migratingDataSource = buildMigratingDataSource(element, dataSourceId, beanRegistry);
            beanRegistry.registerBeanDefinition(dataSourceId, migratingDataSource);
            return null;
        }

        private BeanDefinition buildMigratingDataSource(Element element, String dataSourceId, BeanDefinitionRegistry beanRegistry) {
            BeanDefinitionBuilder migratingDataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(MigratingDataSource.class);
            migratingDataSourceBuilder.addPropertyValue("delegate", beanRegistry.getBeanDefinition(dataSourceId));
            migratingDataSourceBuilder.addPropertyValue("username", element.getAttribute("username"));
            migratingDataSourceBuilder.addPropertyValue("password", element.getAttribute("password"));
            addMigratorProperty(element, migratingDataSourceBuilder);
            return migratingDataSourceBuilder.getBeanDefinition();
        }

        private void addMigratorProperty(Element element, BeanDefinitionBuilder dataSourceBuilder) {
            if (element.hasAttribute("migrator")) {
                dataSourceBuilder.addPropertyReference("migrator", element.getAttribute("migrator"));
            } else {
                dataSourceBuilder.addPropertyValue("migrator", buildLiquibaseMigrator(element));
            }
        }

        private BeanDefinition buildLiquibaseMigrator(Element element) {
            BeanDefinitionBuilder liquibaseMigratorBuilder = BeanDefinitionBuilder.genericBeanDefinition(LiquibaseMigrator.class);
            liquibaseMigratorBuilder.addPropertyValue("changeLogPath", element.getAttribute("path"));
            return liquibaseMigratorBuilder.getBeanDefinition();
        }

    }

}
