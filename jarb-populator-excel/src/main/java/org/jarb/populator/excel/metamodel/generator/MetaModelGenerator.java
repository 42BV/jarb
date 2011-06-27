package org.jarb.populator.excel.metamodel.generator;

import java.util.Collection;

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
    
    /**
     * Generate a meta model for all entities.
     * @return new meta model
     */
    MetaModel generateFor(Collection<Class<?>> entityClasses);

}
