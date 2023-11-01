package nl._42.jarb.constraint.violation.resolver;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.utils.jdbc.DatabaseProduct;
import nl._42.jarb.utils.jdbc.DatabaseProductSpecific;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static nl._42.jarb.utils.Asserts.notNull;

/**
 * Chain of responsibility for constraint violation resolvers. Whenever a violation
 * resolver could not resolve the violation, our next resolver is invoked. This
 * process continues until a valid constraint violation gets resolved, or we run
 * out of violation resolvers, in which case a {@code null} is returned.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class ViolationResolverChain implements DatabaseConstraintViolationResolver {
    
    private final Logger logger = LoggerFactory.getLogger(ViolationResolverChain.class);

    private final List<DatabaseConstraintViolationResolver> resolvers;

    public ViolationResolverChain() {
        this(List.of());
    }

    private ViolationResolverChain(List<DatabaseConstraintViolationResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        return resolvers.stream()
            .filter(Objects::nonNull)
            .map(resolver -> resolve(throwable, resolver))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private DatabaseConstraintViolation resolve(Throwable throwable, DatabaseConstraintViolationResolver resolver) {
        logger.debug("Attempting to resolve violation with: {}", resolver);
        DatabaseConstraintViolation violation = resolver.resolve(throwable);
        if (violation != null) {
            logger.debug("Violation was resolved by: {}", resolver);
        }
        return violation;
    }

    /**
     * Add a violation resolver to the back of this chain.
     * @param resolver violation resolver instance we are adding
     * @return {@code this} instance, enabling the use of method chaining
     */
    public ViolationResolverChain add(DatabaseConstraintViolationResolver resolver) {
        notNull(resolver, "Cannot add a null resolver to the chain.");
        logger.debug("Registered resolver {} to chain.", resolver);

        var extended = new ArrayList<>(resolvers);
        extended.add(resolver);
        return new ViolationResolverChain(extended);
    }
    
    /**
     * Add a violation resolver to the back of this chain, when supported.
     * @param resolver violation resolver instance we are adding
     * @param databaseProduct the database product
     * @return {@code this} instance, enabling the use of method chaining
     */
    public ViolationResolverChain addIfSupported(DatabaseConstraintViolationResolver resolver, DatabaseProduct databaseProduct) {
    	if (isSupported(resolver, databaseProduct)) {
            return add(resolver);
        }
    	return this;
    }
    
    private boolean isSupported(DatabaseConstraintViolationResolver resolver, DatabaseProduct product) {
        boolean supported = true;
        if (resolver instanceof DatabaseProductSpecific specific) {
            supported = specific.supports(product);
        }
        return supported;
    }
    
}
