package nl._42.jarb.constraint.violation.resolver;

import nl._42.jarb.constraint.violation.resolver.vendor.H2ViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.HsqlViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.MysqlViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.OracleViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.PostgresViolationResolver;
import nl._42.jarb.utils.jdbc.DatabaseProduct;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Default violation resolver that lazily registers all custom and default
 * resolvers in a chain. Because the resolver is lazy, the registration is
 * done on first usage rather than on initialization.
 * 
 * @author Jeroen van Schagen
 */
public class ConfigurableViolationResolver extends LazyInitViolationResolver {

	private final DataSource dataSource;

	private final List<DatabaseConstraintViolationResolver> resolvers = new ArrayList<>();

    public ConfigurableViolationResolver(DataSource dataSource) {
        this.dataSource = dataSource;
	}

	public ConfigurableViolationResolver add(DatabaseConstraintViolationResolver resolver) {
		this.resolvers.add(resolver);
		return this;
	}

	@Override
	protected DatabaseConstraintViolationResolver init() {
        DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);

		ViolationResolverChain chain = new ViolationResolverChain()
			.addIfSupported(new H2ViolationResolver(), product)
			.addIfSupported(new HsqlViolationResolver(), product)
			.addIfSupported(new MysqlViolationResolver(), product)
			.addIfSupported(new OracleViolationResolver(), product)
			.addIfSupported(new PostgresViolationResolver(), product)
			.add(new HibernateViolationResolver());

		for (DatabaseConstraintViolationResolver resolver : resolvers) {
			chain = chain.add(resolver);
		}

		return chain;
	}

}
