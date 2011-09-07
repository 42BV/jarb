package org.jarbframework.validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import junit.framework.Assert;

import org.jarbframework.validation.domain.Car;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Ensure that database constraint logic is included in the {@link Validator}.
 * Constraint information has already been inserted by: create_constraint_metadata.sql
 * 
 * @author Jeroen van Schagen
 * @since 24-05-2011
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class DatabaseConstraintValidatorTest {

    @Autowired
    private Validator validator;

    /**
     * Assert that a not null database constraint is validated.
     */
    @Test
    public void testViolateNotNull() {
        Car carWithoutLicense = new Car();
        Set<ConstraintViolation<Car>> violations = validator.validate(carWithoutLicense);
        Assert.assertEquals(1, violations.size());
        ConstraintViolation<Car> licenseViolation = violations.iterator().next();
        Assert.assertEquals("licenseNumber", licenseViolation.getPropertyPath().toString());
        Assert.assertEquals("cannot be null", licenseViolation.getMessage());
    }

    /**
     * Assert that a string based length database constraint is validated.
     */
    @Test
    public void testViolateStringMaxLength() {
        Car carWithoutLicense = new Car();
        carWithoutLicense.setLicenseNumber("longerthansixcharacters");
        Set<ConstraintViolation<Car>> violations = validator.validate(carWithoutLicense);
        Assert.assertEquals(1, violations.size());
        ConstraintViolation<Car> licenseViolation = violations.iterator().next();
        Assert.assertEquals("licenseNumber", licenseViolation.getPropertyPath().toString());
        Assert.assertEquals("length cannot be greater than 6", licenseViolation.getMessage());
    }

    /**
     * Assert that a number based length database constraint is validated.
     */
    @Test
    public void testViolateNumberMaxLength() {
        Car carWithHighPrice = new Car();
        carWithHighPrice.setLicenseNumber("abcdef");
        carWithHighPrice.setPrice(1000000D); // 7 digits
        Set<ConstraintViolation<Car>> violations = validator.validate(carWithHighPrice);
        Assert.assertEquals(1, violations.size());
        ConstraintViolation<Car> licenseViolation = violations.iterator().next();
        Assert.assertEquals("price", licenseViolation.getPropertyPath().toString());
        Assert.assertEquals("length cannot be greater than 6", licenseViolation.getMessage());
    }

    /**
     * Assert that fraction length is checked.
     */
    @Test
    public void testViolateMaxFractionLength() {
        Car carWithHighPrice = new Car();
        carWithHighPrice.setLicenseNumber("abcdef");
        carWithHighPrice.setPrice(42.123); // 3 digits in fraction
        Set<ConstraintViolation<Car>> violations = validator.validate(carWithHighPrice);
        Assert.assertEquals(1, violations.size());
        ConstraintViolation<Car> licenseViolation = violations.iterator().next();
        Assert.assertEquals("price", licenseViolation.getPropertyPath().toString());
        Assert.assertEquals("cannot have more than 2 numbers behind the comma", licenseViolation.getMessage());
    }

    /**
     * Assert that multiple database constraint violations can be validated at once.
     */
    @Test
    public void testMultipleViolations() {
        Car unknownOverpricedCar = new Car(); // Missing a license number
        unknownOverpricedCar.setPrice(42000.123); // 3 digits in fraction, 8 digits in total
        Set<ConstraintViolation<Car>> violations = validator.validate(unknownOverpricedCar);
        Assert.assertEquals(3, violations.size());
        Collection<String> messages = collectMessage(violations);
        Assert.assertTrue(messages.contains("cannot be null"));
        Assert.assertTrue(messages.contains("length cannot be greater than 6"));
        Assert.assertTrue(messages.contains("cannot have more than 2 numbers behind the comma"));
    }

    /**
     * Retrieve the messages of each provided constraint violation.
     * @param violations collection of constraint violations
     * @return messages of each provided constraint violation
     */
    private Collection<String> collectMessage(Set<ConstraintViolation<Car>> violations) {
        Collection<String> messages = new HashSet<String>();
        for (ConstraintViolation<?> violation : violations) {
            messages.add(violation.getMessage());
        }
        return messages;
    }

}
