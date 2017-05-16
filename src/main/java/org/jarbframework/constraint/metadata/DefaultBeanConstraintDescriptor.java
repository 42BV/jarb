/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.ClassPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabaseGeneratedPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabasePropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DigitsPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.EnumPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.LengthPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.MinMaxNumberAnnotationPropertyEnhancer;
import org.jarbframework.constraint.metadata.enhance.MinMaxNumberPropertyEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotEmptyPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotNullPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.PatternPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.PropertyTypeEnhancer;
import org.jarbframework.utils.Classes;

/**
 * Bean constraint descriptor with all default enhancers. 
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class DefaultBeanConstraintDescriptor extends BeanConstraintDescriptor {
    
    private static final String JAVAX_VALIDATION_PACKAGE = "javax.validation";
    private static final String HIBERNATE_VALIDATION_PACKAGE = "org.hibernate.validator";
    private static final String JODA_TIME_PACKAGE = "org.joda.time";

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
        if (Classes.hasClass(JODA_TIME_PACKAGE)) {
            registerJodaTimeEnhancers();
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
        register(new MinMaxNumberAnnotationPropertyEnhancer());
    }

    private void registerHibernateValidationEnhancers() {
        register(new LengthPropertyConstraintEnhancer());
        register(new NotEmptyPropertyConstraintEnhancer());

        register(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.Email.class, "email"));
        register(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.CreditCardNumber.class, "credid_card"));
        register(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.URL.class, "url"));
    }

    private void registerJodaTimeEnhancers() {
        register(new ClassPropertyTypeEnhancer(org.joda.time.DateTime.class, "date-time"));
        register(new ClassPropertyTypeEnhancer(org.joda.time.LocalDate.class, "date"));
        register(new ClassPropertyTypeEnhancer(org.joda.time.LocalDateTime.class, "date-time-local"));
    }

    private void registerDatabaseEnhancers(BeanMetadataRepository beanMetadataRepository) {
        register(new DatabasePropertyConstraintEnhancer(beanMetadataRepository));
        register(new DatabaseGeneratedPropertyConstraintEnhancer());
    }

}
