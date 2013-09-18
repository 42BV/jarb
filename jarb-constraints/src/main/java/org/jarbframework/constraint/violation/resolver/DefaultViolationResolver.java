package org.jarbframework.constraint.violation.resolver;

import java.util.Set;

import javax.sql.DataSource;

import org.jarbframework.constraint.violation.resolver.product.DatabaseProduct;
import org.jarbframework.constraint.violation.resolver.vendor.H2ViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.MysqlViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.OracleViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.PostgresViolationResolver;
import org.jarbframework.utils.Classes;
import org.springframework.beans.BeanUtils;

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

        ViolationResolverChain chain = new ViolationResolverChain();
        addCustomResolvers(chain, databaseProduct);
        addDefaultResolvers(chain, databaseProduct);
        return chain;
	}

	private void addCustomResolvers(ViolationResolverChain chain, DatabaseProduct databaseProduct) {
        Set<Class<?>> resolverClasses = Classes.getAllOfType(basePackage, DatabaseConstraintViolationResolver.class);
        for (Class<?> resolverClass : resolverClasses) {
        	chain.addToChainWhenSupported(buildCustomResolver(resolverClass), databaseProduct);
        }
    }

	private DatabaseConstraintViolationResolver buildCustomResolver(Class<?> resolverClass) {
		return (DatabaseConstraintViolationResolver) BeanUtils.instantiateClass(resolverClass);
	}

	private void addDefaultResolvers(ViolationResolverChain chain, DatabaseProduct databaseProduct) {
		chain.addToChainWhenSupported(new H2ViolationResolver(), databaseProduct);
        chain.addToChainWhenSupported(new HsqlViolationResolver(), databaseProduct);
        chain.addToChainWhenSupported(new MysqlViolationResolver(), databaseProduct);
        chain.addToChainWhenSupported(new OracleViolationResolver(), databaseProduct);
        chain.addToChainWhenSupported(new PostgresViolationResolver(), databaseProduct);
        chain.addToChain(new HibernateViolationResolver());
	}
	
}
