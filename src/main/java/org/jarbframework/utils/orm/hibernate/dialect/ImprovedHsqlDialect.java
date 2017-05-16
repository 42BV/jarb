package org.jarbframework.utils.orm.hibernate.dialect;

import java.sql.Types;

import org.hibernate.dialect.HSQLDialect;

/**
 * SQL dialect for HSQLDB (HyperSQL), containing various bug fixes.
 *
 * @author Bas de Vos
 * @since Sep 8, 2011
 */
public class ImprovedHsqlDialect extends HSQLDialect {

    public ImprovedHsqlDialect() {
        super();
        // Resolves Hibernate bug (https://hibernate.onjira.com/browse/HHH-1598)
        registerColumnType(Types.BIT, "boolean");
        // Resolves Liquibase 2.0.1 bug (http://liquibase.jira.com/browse/CORE-962)
        registerColumnType(Types.NUMERIC, "decimal($p,$s)");
    }

}
