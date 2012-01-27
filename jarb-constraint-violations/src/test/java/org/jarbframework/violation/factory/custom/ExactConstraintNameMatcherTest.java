/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.violation.factory.custom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExactConstraintNameMatcherTest {
    private ConstraintNameMatcher matcher = ConstraintNameMatcher.exact("uk_persons_name");

    @Test
    public void testMatch() {
        assertTrue(matcher.matches("uk_persons_name"));
    }

    @Test
    public void testMismatch() {
        assertFalse(matcher.matches("uk_countries_name"));
    }

    @Test
    public void testMixedCasing() {
        assertTrue(matcher.matches("UK_PERSONS_NAME"));
        matcher.setIgnoreCase(false);
        assertFalse(matcher.matches("UK_PERSONS_NAME"));
    }

}
