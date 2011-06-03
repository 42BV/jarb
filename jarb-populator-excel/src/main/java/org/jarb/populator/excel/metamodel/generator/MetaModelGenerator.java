package org.jarb.populator.excel.metamodel.generator;

import org.jarb.populator.excel.metamodel.MetaModel;

/**
 * Generates a {@link MetaModel}.
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
     * Generate a meta model for a collection of entities.
     * @param entityClasses type of entities to include in our meta model
     * @return new meta model for our specified entities
     */
    MetaModel generateFor(Iterable<Class<?>> entityClasses);

    /**
     * Generate a meta model for a var-arg of entities.
     * @param entityClasses type of entities to include in our meta model
     * @return new meta model for our specified entities
     */
    MetaModel generateFor(Class<?>... entityClasses);

}
