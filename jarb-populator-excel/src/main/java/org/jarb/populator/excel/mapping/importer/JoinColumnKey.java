package org.jarb.populator.excel.mapping.importer;


/**
 * Sets and returns key value belonging to a JoinColumn instance, extends from Key.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public class JoinColumnKey extends Key {
    /** Foreign key to many-side. */
    private Object foreignKey;

    /**
     * Sets key value to many-side.
     * @param foreignKeyObject Key object with reference to many-side
     */
    public void setKeyValue(final Object foreignKeyObject) {
        this.foreignKey = foreignKeyObject;
    }

    /**
     * Returns key value of key to many-side.
     * @return Key value to many-side as an integer
     */
    public Object getKeyValue() {
        return foreignKey;
    }
}
