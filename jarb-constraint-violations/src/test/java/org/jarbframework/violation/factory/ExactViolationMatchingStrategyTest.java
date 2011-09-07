/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.violation.factory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.violation.factory.ExactViolationMatchingStrategy;
import org.junit.Before;
import org.junit.Test;

public class ExactViolationMatchingStrategyTest {

    private ExactViolationMatchingStrategy strategy;

    @Before
    public void setUp() {
        strategy = new ExactViolationMatchingStrategy();
    }

    @Test
    public void testMatch() {
        assertTrue(strategy.nameMatches("uk_persons_name", "uk_persons_name"));
    }

    @Test
    public void testMismatch() {
        assertFalse(strategy.nameMatches("uk_persons_name", "uk_countries_name"));
    }

    @Test
    public void testMixedCasing() {
        assertTrue(strategy.nameMatches("UK_PERSONS_NAME", "uk_persons_name"));
        strategy.setCaseSensitive(true);
        assertFalse(strategy.nameMatches("UK_PERSONS_NAME", "uk_persons_name"));
    }

}
