package org.jarbframework.populator.excel.mapping;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;

/**
 * Convert a string into a boolean.
 * @author Jeroen van Schagen
 * @since 05-05-2011
 */
public class StringToBooleanConverter implements Converter<String, Boolean> {
    private static final Collection<String> TRUE_SYNONYMS = Arrays.asList("t", "on", "yes", "ja", "y", "j", "1");
    private static final Collection<String> FALSE_SYNONYMS = Arrays.asList("f", "off", "no", "nee", "n", "0");
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean convert(String source) {
        String lowerCaseSource = source.toLowerCase();
        if (TRUE_SYNONYMS.contains(lowerCaseSource)) {
            return Boolean.TRUE;
        }
        if (FALSE_SYNONYMS.contains(lowerCaseSource)) {
            return Boolean.FALSE;
        }
        return asBoolean(lowerCaseSource);
    }

    /**
     * Convert some string value into a boolean. Whenever the value
     * is not a valid boolean, a runtime exception will be thrown.
     * @param value string based boolean value
     * @return the boolean value
     */
    private Boolean asBoolean(String source) {
        if (source.equals(TRUE_VALUE)) {
            return Boolean.TRUE;
        } else if (source.equals(FALSE_VALUE)) {
            return Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
        }
    }
}
