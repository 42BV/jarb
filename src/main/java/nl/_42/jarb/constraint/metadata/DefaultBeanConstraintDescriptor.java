/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata;

import nl._42.jarb.constraint.metadata.database.BeanMetadataRepository;
import nl._42.jarb.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import nl._42.jarb.constraint.metadata.enhance.ClassPropertyTypeEnhancer;
import nl._42.jarb.constraint.metadata.enhance.DatabaseGeneratedPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.DatabasePropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.DigitsPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.EnumPropertyTypeEnhancer;
import nl._42.jarb.constraint.metadata.enhance.LengthPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.MaxPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.MinMaxNumberPropertyEnhancer;
import nl._42.jarb.constraint.metadata.enhance.MinPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.NotEmptyPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.NotNullPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.PatternPropertyConstraintEnhancer;
import nl._42.jarb.constraint.metadata.enhance.PropertyTypeEnhancer;
import nl._42.jarb.utils.Classes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Bean constraint descriptor with all default enhancers. 
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class DefaultBeanConstraintDescriptor extends BeanConstraintDescriptor {
    
    private static final String JAVAX_VALIDATION_PACKAGE     = "javax.validation";
    private static final String HIBERNATE_VALIDATION_PACKAGE = "org.hibernate.validator";

    public DefaultBeanConstraintDescriptor(BeanMetadataRepository beanMetadataRepository) {
        registerHandlers(beanMetadataRepository);
    }
    
    private void registerHandlers(BeanMetadataRepository beanMetadataRepository) {
        registerDefaultEnhancers();

        if (Classes.hasPackage(JAVAX_VALIDATION_PACKAGE)) {
            registerJavaxValidationEnhancers();
        }

        if (Classes.hasPackage(HIBERNATE_VALIDATION_PACKAGE)) {
            registerHibernateValidationEnhancers();
        }

        if (beanMetadataRepository != null) {
            registerDatabaseEnhancers(beanMetadataRepository);
        }
    }

    private void registerDefaultEnhancers() {
        register(new ClassPropertyTypeEnhancer(String.class, "text"));
        register(new ClassPropertyTypeEnhancer(char.class, "text"));
        register(new ClassPropertyTypeEnhancer(Date.class, "date"));
        register(new ClassPropertyTypeEnhancer(LocalDate.class, "date"));
        register(new ClassPropertyTypeEnhancer(LocalDateTime.class, "datetime"));
        register(new ClassPropertyTypeEnhancer(Number.class, "number"));
        register(new ClassPropertyTypeEnhancer(byte.class, "number"));
        register(new ClassPropertyTypeEnhancer(short.class, "number"));
        register(new ClassPropertyTypeEnhancer(int.class, "number"));
        register(new ClassPropertyTypeEnhancer(long.class, "number"));
        register(new ClassPropertyTypeEnhancer(float.class, "number"));
        register(new ClassPropertyTypeEnhancer(double.class, "number"));
        register(new ClassPropertyTypeEnhancer(Boolean.class, "boolean"));
        register(new ClassPropertyTypeEnhancer(boolean.class, "boolean"));

        register(new PropertyTypeEnhancer());
        register(new EnumPropertyTypeEnhancer());
        
        register(new MinMaxNumberPropertyEnhancer(Byte.class, Byte.MIN_VALUE, Byte.MAX_VALUE));
        register(new MinMaxNumberPropertyEnhancer(Short.class, Short.MIN_VALUE, Short.MAX_VALUE));
        register(new MinMaxNumberPropertyEnhancer(Integer.class, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new MinMaxNumberPropertyEnhancer(Long.class, Long.MIN_VALUE, Long.MAX_VALUE));
    }

    private void registerJavaxValidationEnhancers() {
        register(new NotNullPropertyConstraintEnhancer());
        register(new PatternPropertyConstraintEnhancer());
        register(new DigitsPropertyConstraintEnhancer());

        register(new MinPropertyConstraintEnhancer<>(
            javax.validation.constraints.Min.class,
            (annotation) -> annotation.value()
        ));

        register(new MinPropertyConstraintEnhancer<>(
            javax.validation.constraints.DecimalMin.class,
            (annotation) -> new BigDecimal(annotation.value())
        ));

        register(new MinPropertyConstraintEnhancer<>(
            javax.validation.constraints.Positive.class,
            (annotation) -> 0
        ));

        register(new MinPropertyConstraintEnhancer<>(
            javax.validation.constraints.PositiveOrZero.class,
            (annotation) -> 0
        ));

        register(new MaxPropertyConstraintEnhancer<>(
            javax.validation.constraints.Max.class,
            (annotation) -> annotation.value()
        ));

        register(new MaxPropertyConstraintEnhancer<>(
            javax.validation.constraints.DecimalMax.class,
            (annotation) -> new BigDecimal(annotation.value())
        ));

        register(new MaxPropertyConstraintEnhancer<>(
            javax.validation.constraints.Negative.class,
            (annotation) -> 0
        ));

        register(new MaxPropertyConstraintEnhancer<>(
            javax.validation.constraints.NegativeOrZero.class,
            (annotation) -> 0
        ));

        register(new LengthPropertyConstraintEnhancer<>(
            javax.validation.constraints.Size.class,
            (annotation) -> annotation.min(),
            (annotation) -> annotation.max()
        ));

        register(new NotEmptyPropertyConstraintEnhancer(javax.validation.constraints.NotEmpty.class));
        register(new NotEmptyPropertyConstraintEnhancer(javax.validation.constraints.NotBlank.class));

        register(new AnnotationPropertyTypeEnhancer(javax.validation.constraints.Email.class, "email"));
    }

    private void registerHibernateValidationEnhancers() {
        register(new LengthPropertyConstraintEnhancer<>(
            org.hibernate.validator.constraints.Length.class,
            (annotation) -> annotation.min(),
            (annotation) -> annotation.max()
        ));

        register(new NotEmptyPropertyConstraintEnhancer(org.hibernate.validator.constraints.NotEmpty.class));

        register(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.Email.class, "email"));
        register(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.CreditCardNumber.class, "credid_card"));
        register(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.URL.class, "url"));
    }

    private void registerDatabaseEnhancers(BeanMetadataRepository beanMetadataRepository) {
        register(new DatabasePropertyConstraintEnhancer(beanMetadataRepository));
        register(new DatabaseGeneratedPropertyConstraintEnhancer());
    }

}
