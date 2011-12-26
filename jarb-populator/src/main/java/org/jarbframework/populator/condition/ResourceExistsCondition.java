package org.jarbframework.populator.condition;

import static org.jarbframework.utils.Asserts.notNull;

import org.springframework.core.io.Resource;

/**
 * Verifies whether the specified resource exists.
 * @author Jeroen van Schagen
 * @since 21-06-2011
 */
public class ResourceExistsCondition implements Condition {
    
    /** Resource being checked on existence. **/
    private final Resource resource;

    /**
     * Construct a new {@link ResourceExistsCondition}.
     * @param resource the resource being checked on existence
     */
    public ResourceExistsCondition(Resource resource) {
        this.resource = notNull(resource, "Cannot check the existence of a null resource");
    }

    @Override
    public ConditionEvaluation evaluate() {
        ConditionEvaluation evaluation = new ConditionEvaluation();
        evaluation.state(resource.exists(), "Resource '" + resource + "' does not exist.");
        return evaluation;
    }
}
