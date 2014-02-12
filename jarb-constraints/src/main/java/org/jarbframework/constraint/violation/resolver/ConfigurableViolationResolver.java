package org.jarbframework.constraint.violation.resolver;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Set;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.vendor.H2ViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.PostgresViolationResolver;
import org.jarbframework.utils.Classes;
import org.jarbframework.utils.DatabaseProduct;
import org.springframework.beans.BeanUtils;

/**
 * Default violation resolver that lazily registers all custom and default
 * resolvers in a chain. Because the resolver is lazy, the registration is
 * done on first usage rather than on initialization.
 * 
 * @author Jeroen van Schagen
 */
public class ConfigurableViolationResolver extends LazyInitViolationResolver {
	
    private final ViolationResolverChain resolvers = new ViolationResolverChain();

	private final DataSource dataSource;
	
	private final String basePackage;

	public ConfigurableViolationResolver(DataSource dataSource, String basePackage) {
		this.dataSource = dataSource;
		this.basePackage = basePackage;
	}

	@Override
	protected DatabaseConstraintViolationResolver init() {
        final DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);

        registerCustomResolvers(product);
        registerDefaultResolvers(product);
        return resolvers;
	}

    public void registerResolver(DatabaseConstraintViolationResolver resolver) {
        resolvers.addToChain(resolver);
    }

    private void registerCustomResolvers(DatabaseProduct databaseProduct) {
        if (isNotBlank(basePackage)) {
            Set<Class<?>> resolverClasses = Classes.getAllOfType(basePackage, DatabaseConstraintViolationResolver.class);
            for (Class<?> resolverClass : resolverClasses) {
                resolvers.addToChainWhenSupported(newResolver(resolverClass), databaseProduct);
            }
        }
    }

	private DatabaseConstraintViolationResolver newResolver(Class<?> resolverClass) {
		return (DatabaseConstraintViolationResolver) BeanUtils.instantiateClass(resolverClass);
	}

    private void registerDefaultResolvers(DatabaseProduct product) {
        resolvers.addToChainWhenSupported(new H2ViolationResolver(), product);
        resolvers.addToChainWhenSupported(new HsqlViolationResolver(), product);
        resolvers.addToChainWhenSupported(new MysqlViolationResolver(), product);
        resolvers.addToChainWhenSupported(new OracleViolationResolver(), product);
        resolvers.addToChainWhenSupported(new PostgresViolationResolver(), product);
        resolvers.addToChain(new HibernateViolationResolver());
	}
	
}
