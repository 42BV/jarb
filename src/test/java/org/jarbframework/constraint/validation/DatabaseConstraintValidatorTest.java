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

import org.jarbframework.constraint.domain.Address;
import org.jarbframework.constraint.domain.AwesomeCar;
import org.jarbframework.constraint.domain.ChangeAddressCommand;
import org.jarbframework.constraint.domain.Contact;
import org.jarbframework.constraint.domain.Person;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@Ignore("Tests are failing right before the release build to Sonatype OSS")
public class DatabaseConstraintValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    public void testValid() {
        AwesomeCar car = new AwesomeCar("AB1337");

        Set<ConstraintViolation<AwesomeCar>> violations = validator.validate(car);
        assertTrue("Expected no violations", violations.isEmpty());
    }

    @Test
    public void testNotNull() {
        AwesomeCar carWithoutLicense = new AwesomeCar(null);

        Set<ConstraintViolation<AwesomeCar>> violations = validator.validate(carWithoutLicense);
        assertEquals(1, violations.size());

        ConstraintViolation<AwesomeCar> violation = violations.iterator().next();
        assertEquals("licenseNumber", violation.getPropertyPath().toString());
        assertEquals("cannot be null", violation.getMessage());
    }

    @Test
    public void testViolateStringMaxLength() {
        AwesomeCar carWithoutLicense = new AwesomeCar("longerthansixcharacters");

        Set<ConstraintViolation<AwesomeCar>> violations = validator.validate(carWithoutLicense);
        assertEquals(1, violations.size());

        ConstraintViolation<AwesomeCar> licenseViolation = violations.iterator().next();
        assertEquals("licenseNumber", licenseViolation.getPropertyPath().toString());
        assertEquals("length cannot be greater than 6", licenseViolation.getMessage());
    }

    @Test
    public void testViolateNumberMaxLength() {
        AwesomeCar carWithHighPrice = new AwesomeCar("abcdef");
        carWithHighPrice.setPrice(1000000D);

        Set<ConstraintViolation<AwesomeCar>> violations = validator.validate(carWithHighPrice);
        assertEquals(1, violations.size());

        ConstraintViolation<AwesomeCar> licenseViolation = violations.iterator().next();
        assertEquals("price", licenseViolation.getPropertyPath().toString());
        assertEquals("length cannot be greater than 6", licenseViolation.getMessage());
    }

    @Test
    public void testViolateMaxFractionLength() {
        AwesomeCar carWithHighPrice = new AwesomeCar("abcdef");
        carWithHighPrice.setPrice(42.123);

        Set<ConstraintViolation<AwesomeCar>> violations = validator.validate(carWithHighPrice);
        assertEquals(1, violations.size());

        ConstraintViolation<AwesomeCar> licenseViolation = violations.iterator().next();
        assertEquals("price", licenseViolation.getPropertyPath().toString());
        assertEquals("cannot have more than 2 numbers behind the comma", licenseViolation.getMessage());
    }

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
    
    @Test
    public void testEmbeddedViolationWithoutEntity() {
    	ChangeAddressCommand command = new ChangeAddressCommand();
        Address addressWithoutCity = new Address("Testweg 61", null);
        command.setAddress(addressWithoutCity);

        Set<ConstraintViolation<ChangeAddressCommand>> violations = validator.validate(command);
        assertEquals(1, violations.size());

        ConstraintViolation<ChangeAddressCommand> violation = violations.iterator().next();
        assertEquals("address.city", violation.getPropertyPath().toString());
        assertEquals("cannot be null", violation.getMessage());
    }

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
        AwesomeCar unknownOverpricedCar = new AwesomeCar(null);
        unknownOverpricedCar.setPrice(42000.123);

        assertThat(validator.validate(unknownOverpricedCar),
                containsInAnyOrder(
                        hasProperty("message", equalTo("cannot be null")),
                        hasProperty("message", equalTo("length cannot be greater than 6")),
                        hasProperty("message", equalTo("cannot have more than 2 numbers behind the comma"))
                ));
    }

}
