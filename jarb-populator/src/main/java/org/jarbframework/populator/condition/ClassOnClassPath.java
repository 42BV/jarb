package org.jarbframework.populator.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassOnClassPath implements Condition {
	
	private final Logger logger = LoggerFactory.getLogger(ClassOnClassPath.class);
	
	private final String className;
	
	public ClassOnClassPath(String className) {
		this.className = className;
	}
	
	@Override
	public ConditionEvaluation evaluate() {
		ConditionEvaluation evaluation = new ConditionEvaluation();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Class.forName(className, false, loader);
		} catch(ClassNotFoundException e) {
			String failMessage = "Class '" + className + "' is not on classpath.";
			logger.debug(failMessage, e);
			evaluation.addFailure(failMessage);
		}
		return evaluation;
	}
	
}
