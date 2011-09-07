package org.jarbframework.populator.condition;

import static org.jarbframework.utils.Conditions.notNull;

import org.springframework.core.io.Resource;

/**
 * Verifies whether the specified resource exists.
 * 
 * @author Jeroen van Schagen
 * @since 21-06-2011
 */
public class ResourceExistsConditionChecker implements ConditionChecker {
    private final Resource resource;

    /**
     * Construct a new {@link ResourceExistsConditionChecker}.
     * @param resource the resource being checked on existence
     */
    public ResourceExistsConditionChecker(Resource resource) {
        this.resource = notNull(resource, "Cannot check the existence of a null resource");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConditionCheckResult checkCondition() {
        ConditionCheckResult conditionCheck = new ConditionCheckResult();
        if (!resource.exists()) {
            conditionCheck.fail("Resource '" + resource + "' does not exist.");
        }
        return conditionCheck;
    }

}
