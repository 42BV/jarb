package org.jarb.populator.condition;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Checks if a resource exists.
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
        Assert.notNull(resource);
        this.resource = resource;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ConditionCheckResult checkCondition() {
        ConditionCheckResult result = new ConditionCheckResult();
        result.verifyState(resource.exists(), "Resource '" + resource + "' does not exist.");
        return result;
    }
    
}
