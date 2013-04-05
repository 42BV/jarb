package org.jarbframework.constraint.violation.resolver;

import java.util.Set;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.recognize.DatabaseProduct;
import org.jarbframework.constraint.violation.resolver.recognize.DatabaseProductResolver;
import org.jarbframework.constraint.violation.resolver.recognize.DatabaseProductSpecific;
import org.jarbframework.constraint.violation.resolver.vendor.H2ViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.PostgresViolationResolver;
import org.jarbframework.utils.Classes;
import org.springframework.beans.BeanUtils;

/**
 * Capable of building a default constraint violation resolver.
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseConstraintViolationResolverFactory {
        
    private final DatabaseProductResolver databaseProductResolver = new DatabaseProductResolver();

    public DatabaseConstraintViolationResolver createResolver(String basePackage, DataSource dataSource) {
        final DatabaseProduct product = databaseProductResolver.getDatabaseProduct(dataSource);

        ViolationResolverChain chain = new ViolationResolverChain();
        addCustomResolvers(basePackage, chain, product);
        addVendorResolvers(chain, product);
        chain.addToChain(new HibernateViolationResolver());
        return chain;
    }

    private void addCustomResolvers(String basePackage, ViolationResolverChain chain, DatabaseProduct product) {
        Set<Class<?>> resolverClasses = Classes.getAllOfType(basePackage, DatabaseConstraintViolationResolver.class);
        for (Class<?> resolverClass : resolverClasses) {
            addToChainWhenSupported(chain, resolverClass, product);
        }
    }

    private void addToChainWhenSupported(ViolationResolverChain chain, Class<?> resolverClass, DatabaseProduct product) {
        DatabaseConstraintViolationResolver resolver = (DatabaseConstraintViolationResolver) BeanUtils.instantiateClass(resolverClass);
        addToChainWhenSupported(chain, resolver, product);
    }

    private void addVendorResolvers(ViolationResolverChain chain, DatabaseProduct product) {
        addToChainWhenSupported(chain, new H2ViolationResolver(), product);
        addToChainWhenSupported(chain, new HsqlViolationResolver(), product);
        addToChainWhenSupported(chain, new MysqlViolationResolver(), product);
        addToChainWhenSupported(chain, new OracleViolationResolver(), product);
        addToChainWhenSupported(chain, new PostgresViolationResolver(), product);
    }

    private void addToChainWhenSupported(ViolationResolverChain chain, DatabaseConstraintViolationResolver resolver, DatabaseProduct product) {
        if (isSupported(resolver, product)) {
            chain.addToChain(resolver);
        }
    }
    
    private boolean isSupported(DatabaseConstraintViolationResolver resolver, DatabaseProduct product) {
        boolean supported = true;
        if (resolver instanceof DatabaseProductSpecific) {
            supported = ((DatabaseProductSpecific) resolver).supports(product);
        }
        return supported;
    }

}
