/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.jdbc;

import static org.jarbframework.utils.StringUtils.startsWithIgnoreCase;

/**
 * Registered product types.
 *
 * @author Jeroen van Schagen
 * @since Jun 23, 2015
 */
public enum DatabaseProductType {

    HSQL("HSQL Database Engine"),
    H2("H2"),
    PSQL("Postgres"),
    ORACLE("Oracle"),
    MYSQL("MySQL");

    private final String name;

    private DatabaseProductType(String name) {
        this.name = name;
    }

    public static DatabaseProductType findByName(String name) {
        DatabaseProductType result = null;
        for (DatabaseProductType type : DatabaseProductType.values()) {
            if (startsWithIgnoreCase(name, type.name)) {
                result = type;
                break;
            }
        }
        return result;
    }

}