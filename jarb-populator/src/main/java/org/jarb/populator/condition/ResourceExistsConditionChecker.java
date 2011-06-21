package org.jarb.populator.condition;

import org.jarb.populator.ConditionResult;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class ResourceExistsConditionChecker implements ConditionChecker {
    private final Resource resource;
    
    public ResourceExistsConditionChecker(Resource resource) {
        Assert.notNull(resource);
        this.resource = resource;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ConditionResult checkCondition() {
        ConditionResult result = new ConditionResult();
        result.verifyState(resource.exists(), "Resource '%s' does not exist.", resource);
        return result;
    }
    
}
