package org.jarbframework.populator;

import static org.jarbframework.utils.Asserts.notNull;

import org.springframework.core.io.Resource;

/**
 * Verifies whether the specified resource exists.
 * @author Jeroen van Schagen
 * @since 21-06-2011
 */
public class ResourceExistsConditional implements Conditional {
    private final Resource resource;

    /**
     * Construct a new {@link ResourceExistsConditional}.
     * @param resource the resource being checked on existence
     */
    public ResourceExistsConditional(Resource resource) {
        this.resource = notNull(resource, "Cannot check the existence of a null resource");
    }

    @Override
    public Condition check() {
        Condition condition = new Condition();
        if (!resource.exists()) {
            condition.fail("Resource '" + resource + "' does not exist.");
        }
        return condition;
    }
}
