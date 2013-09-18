package org.jarbframework.utils;

import org.jarbframework.utils.sample.SomeAnnotation;
import org.jarbframework.utils.sample.SomeClass;
import org.jarbframework.utils.sample.SomeInterface;
import org.junit.Assert;
import org.junit.Test;

public class ClassesTest {

	@Test
	public void testOnClasspath() {
		Assert.assertTrue(Classes.isOnClasspath(Classes.class.getName()));
		Assert.assertFalse(Classes.isOnClasspath("some.unknown.ClassName"));
	}
	
	@Test
	public void testFindByType() {
		String basePackage = this.getClass().getPackage().getName();
		Assert.assertTrue(Classes.getAllOfType(basePackage, SomeInterface.class).contains(SomeClass.class));
	}
	
	@Test
	public void testFindByAnnotation() {
		String basePackage = this.getClass().getPackage().getName();
		Assert.assertTrue(Classes.getAllWithAnnotation(basePackage, SomeAnnotation.class).contains(SomeClass.class));
	}

}
