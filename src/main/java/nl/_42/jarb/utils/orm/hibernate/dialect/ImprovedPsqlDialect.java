package nl._42.jarb.utils.orm.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

/**
 * Hibernate 5 text column fixes.
 *
 * @author Jeroen van Schagen
 * @since Jun 27, 2016
 */
public class ImprovedPsqlDialect extends PostgreSQL9Dialect {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        if (sqlCode == Types.CLOB) {
            return LongVarcharTypeDescriptor.INSTANCE;
        }
        return super.getSqlTypeDescriptorOverride(sqlCode);
    }
    
}
