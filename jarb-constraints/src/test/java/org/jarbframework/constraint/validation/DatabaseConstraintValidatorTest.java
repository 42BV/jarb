package org.jarbframework.constraint.validation;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jarbframework.constraint.validation.domain.Address;
import org.jarbframework.constraint.validation.domain.Contact;
import org.jarbframework.constraint.validation.domain.Person;
import org.jarbframework.constraint.validation.domain.ValidatingCar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class DatabaseConstraintValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    public void testValid() {
        ValidatingCar car = new ValidatingCar("AB1337");

        Set<ConstraintViolation<ValidatingCar>> violations = validator.validate(car);
        assertTrue("Expected no violations", violations.isEmpty());
    }

    // Direct property violations

    @Test
    public void testNotNull() {
        ValidatingCar carWithoutLicense = new ValidatingCar(null);

        Set<ConstraintViolation<ValidatingCar>> violations = validator.validate(carWithoutLicense);
        assertEquals(1, violations.size());

        ConstraintViolation<ValidatingCar> violation = violations.iterator().next();
        assertEquals("licenseNumber", violation.getPropertyPath().toString());
        assertEquals("cannot be null", violation.getMessage());
    }

    @Test
    public void testViolateStringMaxLength() {
        ValidatingCar carWithoutLicense = new ValidatingCar("longerthansixcharacters");

        Set<ConstraintViolation<ValidatingCar>> violations = validator.validate(carWithoutLicense);
        assertEquals(1, violations.size());

        ConstraintViolation<ValidatingCar> licenseViolation = violations.iterator().next();
        assertEquals("licenseNumber", licenseViolation.getPropertyPath().toString());
        assertEquals("length cannot be greater than 6", licenseViolation.getMessage());
    }

    @Test
    public void testViolateNumberMaxLength() {
        ValidatingCar carWithHighPrice = new ValidatingCar("abcdef");
        carWithHighPrice.setPrice(1000000D);

        Set<ConstraintViolation<ValidatingCar>> violations = validator.validate(carWithHighPrice);
        assertEquals(1, violations.size());

        ConstraintViolation<ValidatingCar> licenseViolation = violations.iterator().next();
        assertEquals("price", licenseViolation.getPropertyPath().toString());
        assertEquals("length cannot be greater than 6", licenseViolation.getMessage());
    }

    @Test
    public void testViolateMaxFractionLength() {
        ValidatingCar carWithHighPrice = new ValidatingCar("abcdef");
        carWithHighPrice.setPrice(42.123);

        Set<ConstraintViolation<ValidatingCar>> violations = validator.validate(carWithHighPrice);
        assertEquals(1, violations.size());

        ConstraintViolation<ValidatingCar> licenseViolation = violations.iterator().next();
        assertEquals("price", licenseViolation.getPropertyPath().toString());
        assertEquals("cannot have more than 2 numbers behind the comma", licenseViolation.getMessage());
    }

    // Embedded

    @Test
    public void testEmbeddedViolation() {
        Person person = new Person();
        person.setName("Henk");
        Address addressWithoutCity = new Address("Testweg 61", null);
        person.setContact(new Contact(addressWithoutCity));

        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(1, violations.size());

        ConstraintViolation<Person> violation = violations.iterator().next();
        assertEquals("contact.address.city", violation.getPropertyPath().toString());
        assertEquals("cannot be null", violation.getMessage());
    }

    // Alternative scenario's

    /**
     * Assert that multiple database constraint violations can be validated at once.
     * Violations:
     * <ul>
     *  <li>Missing a license number</li>
     *  <li>Price has 3 fraction digits, while only 2 are allowed</li>
     *  <li>Price has 8 total digits, while only 6 are allowed</li>
     * </ul>
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleViolations() {
        ValidatingCar unknownOverpricedCar = new ValidatingCar(null);
        unknownOverpricedCar.setPrice(42000.123);

        assertThat(validator.validate(unknownOverpricedCar),
                containsInAnyOrder(
                        hasProperty("message", equalTo("cannot be null")),
                        hasProperty("message", equalTo("length cannot be greater than 6")),
                        hasProperty("message", equalTo("cannot have more than 2 numbers behind the comma"))
                ));
    }

}
