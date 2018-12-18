package nl._42.jarb.constraint.violation.factory.mapping;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.factory.NameMatchingPredicate;
import nl._42.jarb.constraint.violation.factory.NameMatchingStrategy;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.factory.NameMatchingPredicate;
import nl._42.jarb.constraint.violation.factory.NameMatchingStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NameMatchingPredicateTest {
	
	private NameMatchingPredicate nameMatchingPredicate;
	
	@Before
	public void setUp() {
		nameMatchingPredicate = new NameMatchingPredicate(new String[]{"uk_person_name"}, NameMatchingStrategy.EXACT);
	}

	@Test
	public void testMatch() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation("uk_person_name");
        Assert.assertTrue(nameMatchingPredicate.isSupported(violation));
	}
	
	@Test
	public void testNoMatch() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation("uk_cars_license_number");
        Assert.assertFalse(nameMatchingPredicate.isSupported(violation));
	}
	
	@Test
	public void testNull() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation(DatabaseConstraintType.LENGTH_EXCEEDED);
        Assert.assertFalse(nameMatchingPredicate.isSupported(violation));
	}
	
}
