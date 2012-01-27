package org.jarbframework.populator.condition;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.jarbframework.populator.condition.Condition.ConditionEvaluation;
import org.junit.Test;

public class ClassOnClassPathTest {

    @Test
    public void testExists() {
    	String className = ClassOnClassPath.class.getName();
        ConditionEvaluation evaluation = new ClassOnClassPath(className).evaluate();
        assertTrue(evaluation.isSatisfied());
    }
    
    @Test
    public void testNonExisting() {
    	String className = "some.unknown.WierdClass";
        ConditionEvaluation evaluation = new ClassOnClassPath(className).evaluate();
        assertFalse(evaluation.isSatisfied());
        assertThat(evaluation.getFailures(), contains("Class 'some.unknown.WierdClass' is not on classpath."));
    }
	
}
