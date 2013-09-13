package org.jarbframework.constraint.violation.factory.mapping;

import org.jarbframework.constraint.violation.DatabaseConstraintType;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.NameMatchingPredicate;
import org.jarbframework.constraint.violation.factory.NameMatchingStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NameMatchingPredicateTest {
	
	private NameMatchingPredicate nameMatchingPredicate;
	
	@Before
	public void setUp() {
		nameMatchingPredicate = new NameMatchingPredicate("uk_person_name", NameMatchingStrategy.EXACT);
	}

	@Test
	public void testMatch() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation("uk_person_name");
		Assert.assertTrue(nameMatchingPredicate.apply(violation));
	}
	
	@Test
	public void testNoMatch() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation("uk_cars_license_number");
		Assert.assertFalse(nameMatchingPredicate.apply(violation));
	}
	
	@Test
	public void testNull() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation(DatabaseConstraintType.LENGTH_EXCEEDED);
		Assert.assertFalse(nameMatchingPredicate.apply(violation));
	}
	
}
