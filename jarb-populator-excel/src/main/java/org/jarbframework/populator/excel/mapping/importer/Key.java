package org.jarbframework.populator.excel.mapping.importer;

/**
 * Abstract key class, can set and return foreign key classes.
 * @author Willem Eppen.
 * @author Sander Benschop
 *
 */
public abstract class Key {
    /** Persistent foreign class. */
    private Class<?> foreignClass;

    /** Abstract function to set a key value, implemented by subclasses.
     * @param object keyvalue
     */
    public abstract void setKeyValue(final Object object);

    /**
     * Sets foreign class referring to a certain class.
     * @param foreignClass Persistent foreign class
     */
    public void setForeignClass(Class<?> foreignClass) {
        this.foreignClass = foreignClass;
    }

    /**
     * Returns foreign class referring to a certain class.
     * @return Persistent foreign class
     */
    public Class<?> getForeignClass() {
        return this.foreignClass;
    }

}
