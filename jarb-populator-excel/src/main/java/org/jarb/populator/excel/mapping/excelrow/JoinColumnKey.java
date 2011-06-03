package org.jarb.populator.excel.mapping.excelrow;


/**
 * Sets and returns key value belonging to a JoinColumn instance, extends from Key.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public class JoinColumnKey extends Key {
    /** Foreign key to many-side. */
    private Integer foreignKey;

    /**
     * Sets key value to many-side.
     * @param foreignKeyObject Key object with reference to many-side
     */
    public void setKeyValue(final Object foreignKeyObject) {
        this.foreignKey = (Integer) foreignKeyObject;
    }

    /**
     * Returns key value of key to many-side.
     * @return Key value to many-side as an integer
     */
    public Integer getKeyValue() {
        return foreignKey;
    }
}
