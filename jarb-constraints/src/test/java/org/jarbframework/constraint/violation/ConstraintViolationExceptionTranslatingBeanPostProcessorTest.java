package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jarbframework.constraint.violation.domain.User;
import org.jarbframework.constraint.violation.domain.UserInactiveException;
import org.jarbframework.constraint.violation.domain.UserRepository;
import org.jarbframework.constraint.violation.domain.UsernameAlreadyExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test that exceptions are translated into constraint violation exceptions.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:translation-context.xml" })
public class ConstraintViolationExceptionTranslatingBeanPostProcessorTest {

    @Autowired
    private UserRepository users;

    /**
     * HSQL throws a native exception, stating that "uk_user_name" was violated.
     * Because we registered a custom exception factory for that constraint, our custom
     * "user name already exists" exception should be thrown.
     */
    @Test
    public void testUniqueWithCustomException() {
        User user = new User("iarpro");
        users.add(user);
        User userWithSameName = new User("iarpro");
        try {
            users.add(userWithSameName);
            fail("Expected a license number already exists exception");
        } catch (UsernameAlreadyExistsException e) {
            DatabaseConstraintViolation violation = e.getViolation();
            assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getViolationType());
            assertEquals("uk_users_name", violation.getConstraintName());
        }
    }

    /**
     * HSQL throws a native exception, starting that name cannot be null. Our
     * translator should convert this into the default "not null" violation exception.
     */
    @Test
    public void testNotNullDefaultException() {
        User userWithoutName = new User(null);
        try {
            users.add(userWithoutName);
            fail("Expected a not null exception");
        } catch (NotNullViolationException e) {
            assertEquals("Column 'name' cannot be null.", e.getMessage());
            DatabaseConstraintViolation violation = e.getViolation();
            assertEquals(NOT_NULL, violation.getViolationType());
            assertEquals("name", violation.getColumnName());
        }
    }

    /**
     * Checked exceptions should not be affected by our translation.
     * @throws UserInactiveException always, but expected
     */
    @Test(expected = UserInactiveException.class)
    public void testCheckedExceptionUnaffected() throws UserInactiveException {
        users.throwCheckedException();
    }

    /**
     * Unrelated runtime exceptions should always remain unaffected by our translation.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testOtherRuntimeExceptionUnaffected() {
        users.throwUnsupportedOperationException();
    }

}
