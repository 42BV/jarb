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
public class DefaultViolationResolver extends LazyInitViolationResolver {
	
	private final DataSource dataSource;
	
	private final String basePackage;

	public DefaultViolationResolver(DataSource dataSource, String basePackage) {
		this.dataSource = dataSource;
		this.basePackage = basePackage;
	}

	@Override
	protected DatabaseConstraintViolationResolver init() {
        final DatabaseProduct databaseProduct = DatabaseProduct.fromDataSource(dataSource);

        ViolationResolverChain resolversChain = new ViolationResolverChain();
        addCustomResolvers(resolversChain, databaseProduct);
        addDefaultResolvers(resolversChain, databaseProduct);
        return resolversChain;
	}

	private void addCustomResolvers(ViolationResolverChain resolversChain, DatabaseProduct databaseProduct) {
        if (isNotBlank(basePackage)) {
            Set<Class<?>> resolverClasses = Classes.getAllOfType(basePackage, DatabaseConstraintViolationResolver.class);
            for (Class<?> resolverClass : resolverClasses) {
                resolversChain.addToChainWhenSupported(buildCustomResolver(resolverClass), databaseProduct);
            }
        }
    }

	private DatabaseConstraintViolationResolver buildCustomResolver(Class<?> resolverClass) {
		return (DatabaseConstraintViolationResolver) BeanUtils.instantiateClass(resolverClass);
	}

	private void addDefaultResolvers(ViolationResolverChain resolversChain, DatabaseProduct databaseProduct) {
		resolversChain.addToChainWhenSupported(new H2ViolationResolver(), databaseProduct);
        resolversChain.addToChainWhenSupported(new HsqlViolationResolver(), databaseProduct);
        resolversChain.addToChainWhenSupported(new MysqlViolationResolver(), databaseProduct);
        resolversChain.addToChainWhenSupported(new OracleViolationResolver(), databaseProduct);
        resolversChain.addToChainWhenSupported(new PostgresViolationResolver(), databaseProduct);
        resolversChain.addToChain(new HibernateViolationResolver());
	}
	
}
