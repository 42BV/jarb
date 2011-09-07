package org.jarbframework.populator.excel.mapping.importer;

import java.util.Set;

/**
 * Sets and returns key values belonging to a JoinTable instance, extends from Key.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public class JoinTableKey extends Key {
    /** Set of foreignKeys referring to the many-side. */
    private Set<Integer> foreignKeys;

    /**
     * Sets a single key value.
     * @param foreignKeyObject Foreign key on many-side referring to one-side
     */
    @SuppressWarnings("unchecked")
    public void setKeyValue(final Object foreignKeyObject) {
        this.foreignKeys = (Set<Integer>) foreignKeyObject;
    }

    /**
     * Returns a set with foreign key values on many-side referring to one-side.
     * @return Set of foreign keys
     */
    public Set<Integer> getKeyValues() {
        return this.foreignKeys;
    }

}
