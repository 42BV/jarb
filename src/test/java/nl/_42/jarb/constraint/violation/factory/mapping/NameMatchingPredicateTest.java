package nl._42.jarb.constraint.violation.factory.mapping;

import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.factory.NameMatchingPredicate;
import nl._42.jarb.constraint.violation.factory.NameMatchingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NameMatchingPredicateTest {
	
	private NameMatchingPredicate nameMatchingPredicate;
	
	@BeforeEach
	public void setUp() {
		nameMatchingPredicate = new NameMatchingPredicate(new String[]{"uk_person_name"}, NameMatchingStrategy.EXACT);
	}

	@Test
	public void testMatch() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation("uk_person_name");
		Assertions.assertTrue(nameMatchingPredicate.isSupported(violation));
	}
	
	@Test
	public void testNoMatch() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation("uk_cars_license_number");
		Assertions.assertFalse(nameMatchingPredicate.isSupported(violation));
	}
	
	@Test
	public void testNull() {
		DatabaseConstraintViolation violation = new DatabaseConstraintViolation(DatabaseConstraintType.LENGTH_EXCEEDED);
        Assertions.assertFalse(nameMatchingPredicate.isSupported(violation));
	}
	
}
