package org.jarbframework.constraint.violation.resolver;

import static org.jarbframework.utils.JdbcUtils.doWithConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.product.DatabaseProduct;
import org.jarbframework.constraint.violation.resolver.product.DatabaseProductSpecific;
import org.jarbframework.constraint.violation.resolver.vendor.H2ViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.PostgresViolationResolver;
import org.jarbframework.utils.Classes;
import org.jarbframework.utils.JdbcConnectionCallback;
import org.springframework.beans.BeanUtils;

/**
 * Capable of building a default constraint violation resolver.
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class DatabaseConstraintViolationResolverFactory {
        
    public static DatabaseConstraintViolationResolver buildViolationResolver(String basePackage, DataSource dataSource) {
        final DatabaseProduct databaseProduct = getDatabaseProduct(dataSource);

        ViolationResolverChain violationResolverChain = new ViolationResolverChain();
        addCustomResolvers(violationResolverChain, basePackage, databaseProduct);
        addVendorResolvers(violationResolverChain, databaseProduct);
        violationResolverChain.addToChain(new HibernateViolationResolver());
        return violationResolverChain;
    }

    private static void addCustomResolvers(ViolationResolverChain violationResolverChain, String basePackage, DatabaseProduct product) {
        Set<Class<?>> resolverClasses = Classes.getAllOfType(basePackage, DatabaseConstraintViolationResolver.class);
        for (Class<?> resolverClass : resolverClasses) {
            DatabaseConstraintViolationResolver resolver = BeanUtils.instantiateClass(resolverClass.asSubclass(DatabaseConstraintViolationResolver.class));
            addToChainWhenSupported(violationResolverChain, resolver, product);
        }
    }

    private static void addVendorResolvers(ViolationResolverChain chain, DatabaseProduct product) {
        addToChainWhenSupported(chain, new H2ViolationResolver(), product);
        addToChainWhenSupported(chain, new HsqlViolationResolver(), product);
        addToChainWhenSupported(chain, new MysqlViolationResolver(), product);
        addToChainWhenSupported(chain, new OracleViolationResolver(), product);
        addToChainWhenSupported(chain, new PostgresViolationResolver(), product);
    }

    private static void addToChainWhenSupported(ViolationResolverChain chain, DatabaseConstraintViolationResolver resolver, DatabaseProduct product) {
    	if (isSupported(resolver, product)) {
            chain.addToChain(resolver);
        }
    }
    
    private static boolean isSupported(DatabaseConstraintViolationResolver resolver, DatabaseProduct product) {
        boolean supported = true;
        if (resolver instanceof DatabaseProductSpecific) {
            supported = ((DatabaseProductSpecific) resolver).supports(product);
        }
        return supported;
    }
    
    private static DatabaseProduct getDatabaseProduct(DataSource dataSource) {
        return doWithConnection(dataSource, new JdbcConnectionCallback<DatabaseProduct>() {

            @Override
            public DatabaseProduct doWork(Connection connection) throws SQLException {
                DatabaseMetaData metaData = connection.getMetaData();
                String productName = metaData.getDatabaseProductName();
                String productVersion = metaData.getDatabaseProductVersion();
                return new DatabaseProduct(productName, productVersion);
            }

        });
    }

}
