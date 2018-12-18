package nl._42.jarb.utils.orm.hibernate;

import static org.hibernate.boot.model.naming.Identifier.toIdentifier;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Physical naming strategy for generic usage.
 * 
 * @author Bas de Vos
 */
public class ConventionPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return toIdentifier(addUnderscores(name.getText()), name.isQuoted());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return toIdentifier(addUnderscores(name.getText()), name.isQuoted());
    }

    protected static String addUnderscores(String name) {
        StringBuilder buffer = new StringBuilder(name.replace('.', '_'));
        for (int index = 1; index < buffer.length() - 1; index++) {
            if (isSeparator(buffer, index)) {
                buffer.insert(index++, '_');
            }
        }
        return buffer.toString().toLowerCase();
    }

    private static boolean isSeparator(StringBuilder buffer, int index) {
        char previous = buffer.charAt(index - 1);
        char current = buffer.charAt(index);
        return Character.isLowerCase(previous) && Character.isUpperCase(current);
    }
}
