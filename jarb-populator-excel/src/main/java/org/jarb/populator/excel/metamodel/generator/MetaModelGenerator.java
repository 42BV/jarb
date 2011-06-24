package org.jarb.populator.excel.metamodel.generator;

import org.jarb.populator.excel.metamodel.MetaModel;

/**
 * Generates a {@link MetaModel}.
 * 
 * @author Jeroen van Schagen
 * @since 10-05-2011
 */
public interface MetaModelGenerator {

    /**
     * Generate a meta model for all entities.
     * @return new meta model
     */
    MetaModel generate();

}
