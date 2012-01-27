package org.jarbframework.populator.condition;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.jarbframework.populator.condition.Condition.ConditionEvaluation;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ResourceExistsTest {

    @Test
    public void testExists() {
        Resource existingResource = new ClassPathResource("log4j.xml");
        ConditionEvaluation evaluation = new ResourceExists(existingResource).evaluate();
        assertTrue(evaluation.isSatisfied());
    }
    
    @Test
    public void testNonExisting() {
        Resource nonExistingResource = new ClassPathResource("unknown-resource");
        ConditionEvaluation evaluation = new ResourceExists(nonExistingResource).evaluate();
        assertFalse(evaluation.isSatisfied());
        assertThat(evaluation.getFailures(), contains("Resource 'class path resource [unknown-resource]' does not exist."));
    }
    
}
