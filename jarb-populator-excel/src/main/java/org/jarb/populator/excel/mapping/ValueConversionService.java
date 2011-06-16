package org.jarb.populator.excel.mapping;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * Converts cell values into their desired target type.
 * @author Jeroen van Schagen
 * @since 06-05-2011
 */
public class ValueConversionService {
    private final GenericConversionService genericConversionService;

    /**
     * Construct a new {@link ValueConversionService}.
     */
    public ValueConversionService() {
        genericConversionService = ConversionServiceFactory.createDefaultConversionService();
        addConverter(new StringToBooleanConverter());
    }
    
    /**
     * Include a converter in this conversion service.
     * @param converter converter to add
     */
    protected void addConverter(Converter<?,?> converter) {
        genericConversionService.addConverter(converter);
    }

    /**
     * Convert a cell value in the desired target type. Whenever no conversion
     * is possible, we throw a {@link CouldNotConvertException}.
     * @param <T> desired target type
     * @param source value being converted
     * @param targetType desired target type class
     * @return converted source value
     */
    public <T> T convert(Object source, Class<T> targetType) {
        try {
            return genericConversionService.convert(source, targetType);
        } catch (ConversionFailedException e) {
            // Convert the spring-specific exception into our own format
            throw new CouldNotConvertException(source, targetType, e.getCause());
        }
    }

}
