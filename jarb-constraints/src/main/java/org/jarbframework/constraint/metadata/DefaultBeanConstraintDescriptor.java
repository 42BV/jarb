/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata;

import java.util.Date;

import org.jarbframework.constraint.metadata.database.BeanMetadataRepository;
import org.jarbframework.constraint.metadata.enhance.AnnotationPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.ClassPropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabaseGeneratedPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DatabasePropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.DigitsPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.LengthPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.MinMaxNumberAnnotationPropertyEnhancer;
import org.jarbframework.constraint.metadata.enhance.MinMaxNumberPropertyEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotEmptyPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.NotNullPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.PatternPropertyConstraintEnhancer;
import org.jarbframework.constraint.metadata.enhance.PropertyTypeEnhancer;
import org.jarbframework.constraint.metadata.types.Color;
import org.jarbframework.constraint.metadata.types.Currency;
import org.jarbframework.constraint.metadata.types.Email;
import org.jarbframework.constraint.metadata.types.Password;
import org.jarbframework.constraint.metadata.types.Percentage;
import org.jarbframework.constraint.metadata.types.Phone;
import org.jarbframework.constraint.metadata.types.PropertyType;
import org.jarbframework.constraint.metadata.types.URL;
import org.jarbframework.utils.Classes;
import org.jarbframework.utils.bean.BeanRegistry;
import org.jarbframework.utils.bean.DefaultBeanRegistry;

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
        this(new DefaultBeanRegistry(), beanMetadataRepository);
    }

    public DefaultBeanConstraintDescriptor(BeanRegistry beanRegistry, BeanMetadataRepository beanMetadataRepository) {
        super(beanRegistry);
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
        register(new ClassPropertyTypeEnhancer(Date.class, "date"));
        register(new ClassPropertyTypeEnhancer(Number.class, "number"));

        register(new PropertyTypeEnhancer(PropertyType.class));
        register(new PropertyTypeEnhancer(Color.class));
        register(new PropertyTypeEnhancer(Currency.class));
        register(new PropertyTypeEnhancer(Email.class));
        register(new PropertyTypeEnhancer(Password.class));
        register(new PropertyTypeEnhancer(Percentage.class));
        register(new PropertyTypeEnhancer(Phone.class));
        register(new PropertyTypeEnhancer(URL.class));
        
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
