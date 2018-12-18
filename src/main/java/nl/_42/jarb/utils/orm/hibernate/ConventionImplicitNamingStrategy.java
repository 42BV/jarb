package nl._42.jarb.utils.orm.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

/**
 * Naming strategy that describes our mapping between classes and tables.
 *
 * @author Jeroen van Schagen
 */
public class ConventionImplicitNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {

    private static final String FOREIGN_KEY_SUFFIX = "_id";

    /**
     * {@inheritDoc}
     */
    @Override
    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
        Identifier identifier = super.determineForeignKeyName(source);
        return Identifier.toIdentifier(identifier.getText() + FOREIGN_KEY_SUFFIX, identifier.isQuoted());
    }

}
