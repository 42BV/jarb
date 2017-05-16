package org.jarbframework.constraint.violation.resolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.vendor.H2ViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.PostgresViolationResolver;
import org.jarbframework.utils.ClassScanner;
import org.jarbframework.utils.jdbc.DatabaseProduct;
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
	
    private final Collection<String> basePackages;

	public ConfigurableViolationResolver(DataSource dataSource, String basePackage) {
        this(dataSource, Arrays.asList(basePackage));
	}
    
    public ConfigurableViolationResolver(DataSource dataSource, Collection<String> basePackages) {
        this.dataSource = dataSource;
        this.basePackages = basePackages;
    }

	@Override
	protected DatabaseConstraintViolationResolver init() {
        final DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);

        registerCustomResolvers(product);
        registerDefaultResolvers(product);
        return resolvers;
	}

    public void registerResolver(DatabaseConstraintViolationResolver resolver) {
        resolvers.add(resolver);
    }

    private void registerCustomResolvers(DatabaseProduct databaseProduct) {
        for (String basePackage : basePackages) {
            Set<Class<?>> resolverClasses = ClassScanner.getAllOfType(basePackage, DatabaseConstraintViolationResolver.class);
            for (Class<?> resolverClass : resolverClasses) {
                resolvers.addIfSupported(newResolver(resolverClass), databaseProduct);
            }
        }
    }

	private DatabaseConstraintViolationResolver newResolver(Class<?> resolverClass) {
		return (DatabaseConstraintViolationResolver) BeanUtils.instantiateClass(resolverClass);
	}

    private void registerDefaultResolvers(DatabaseProduct product) {
        resolvers.addIfSupported(new H2ViolationResolver(), product);
        resolvers.addIfSupported(new HsqlViolationResolver(), product);
        resolvers.addIfSupported(new MysqlViolationResolver(), product);
        resolvers.addIfSupported(new OracleViolationResolver(), product);
        resolvers.addIfSupported(new PostgresViolationResolver(), product);
        resolvers.add(new HibernateViolationResolver());
	}
	
}
