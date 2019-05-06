package nl._42.jarb.constraint.violation.resolver;

import nl._42.jarb.constraint.violation.resolver.vendor.H2ViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.HsqlViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.MysqlViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.OracleViolationResolver;
import nl._42.jarb.constraint.violation.resolver.vendor.PostgresViolationResolver;
import nl._42.jarb.utils.jdbc.DatabaseProduct;

import javax.sql.DataSource;

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

	public ConfigurableViolationResolver(DataSource dataSource) {
        this.dataSource = dataSource;
	}

	@Override
	protected DatabaseConstraintViolationResolver init() {
        final DatabaseProduct product = DatabaseProduct.fromDataSource(dataSource);
        registerDefaults(product);
        return resolvers;
	}

    public void register(DatabaseConstraintViolationResolver resolver) {
        resolvers.add(resolver);
    }

    private void registerDefaults(DatabaseProduct product) {
        resolvers.addIfSupported(new H2ViolationResolver(), product);
        resolvers.addIfSupported(new HsqlViolationResolver(), product);
        resolvers.addIfSupported(new MysqlViolationResolver(), product);
        resolvers.addIfSupported(new OracleViolationResolver(), product);
        resolvers.addIfSupported(new PostgresViolationResolver(), product);
        resolvers.add(new HibernateViolationResolver());
	}

}
