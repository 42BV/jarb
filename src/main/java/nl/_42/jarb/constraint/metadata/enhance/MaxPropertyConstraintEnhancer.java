/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Function;

/**
 * Enhances the property description with @Min and @Max values.
 *
 * @author Jeroen van Schagen
 * @since Mar 13, 2014
 */
public class MaxPropertyConstraintEnhancer<A extends Annotation> implements PropertyConstraintEnhancer {

    private final Class<A> annotationType;
    private final Function<A, Number> accessor;

    public MaxPropertyConstraintEnhancer(Class<A> annotationType, Function<A, Number> accessor) {
        this.annotationType = annotationType;
        this.accessor = accessor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enhance(PropertyConstraintDescription description) {
        final Class<?> javaType = description.getJavaType();

        if (Number.class.isAssignableFrom(javaType)) {
            Collection<A> annotations = Annotations.getAnnotations(description.toReference(), annotationType);
            for (A annotation : annotations) {
                Number maximum = accessor.apply(annotation);
                description.addMax(maximum);
            }
        }
    }

}
