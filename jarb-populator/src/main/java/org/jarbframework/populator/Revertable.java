package org.jarbframework.populator;

/**
 * Updates that implement this interface can be reverted.
 * @author Jeroen van Schagen
 * @since 20-12-2011
 */
public interface Revertable {

    /**
     * Revert the {@link #update()} previously executed.
     * @throws Exception whenever the revert causes an unrecoverable fault
     */
    void revert() throws Exception;
    
}
