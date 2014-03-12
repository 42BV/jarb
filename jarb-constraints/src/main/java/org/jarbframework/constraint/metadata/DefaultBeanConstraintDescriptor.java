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

/**
 * Bean constraint descriptor with all default enhancers. 
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class DefaultBeanConstraintDescriptor extends BeanConstraintDescriptor {
    
    private static final String HIBERNATE_VALIDATOR_PACKAGE = "org.hibernate.validator";
    private static final String JAVAX_VALIDATOR_PACKAGE = "javax.validation";

    public DefaultBeanConstraintDescriptor(BeanMetadataRepository beanMetadataRepository) {
        registerDefaultEnhancers();
        
        if (Classes.hasPackage(JAVAX_VALIDATOR_PACKAGE)) {
            registerValidationEnhancers();
        }
        if (Classes.hasPackage(HIBERNATE_VALIDATOR_PACKAGE)) {
            registerHibernateEnhancers();
        }
        
        if (beanMetadataRepository != null) {
            registerDatabaseEnhancers(beanMetadataRepository);
        }
    }

    private void registerDefaultEnhancers() {
        registerEnhancer(new ClassPropertyTypeEnhancer(String.class, "text"));
        registerEnhancer(new ClassPropertyTypeEnhancer(Date.class, "date"));
        registerEnhancer(new ClassPropertyTypeEnhancer(Number.class, "number"));
        
        registerEnhancer(new PropertyTypeEnhancer(PropertyType.class));
        
        registerEnhancer(new PropertyTypeEnhancer(Color.class));
        registerEnhancer(new PropertyTypeEnhancer(Currency.class));
        registerEnhancer(new PropertyTypeEnhancer(Email.class));
        registerEnhancer(new PropertyTypeEnhancer(Password.class));
        registerEnhancer(new PropertyTypeEnhancer(Percentage.class));
        registerEnhancer(new PropertyTypeEnhancer(Phone.class));
        registerEnhancer(new PropertyTypeEnhancer(URL.class));
    }
    
    private void registerValidationEnhancers() {
        registerEnhancer(new NotNullPropertyConstraintEnhancer());
        registerEnhancer(new PatternPropertyConstraintEnhancer());
        registerEnhancer(new DigitsPropertyConstraintEnhancer());
    }

    private void registerHibernateEnhancers() {
        registerEnhancer(new LengthPropertyConstraintEnhancer());
        registerEnhancer(new NotEmptyPropertyConstraintEnhancer());

        registerEnhancer(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.Email.class, "email"));
        registerEnhancer(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.CreditCardNumber.class, "credid_card"));
        registerEnhancer(new AnnotationPropertyTypeEnhancer(org.hibernate.validator.constraints.URL.class, "url"));
    }

    private void registerDatabaseEnhancers(BeanMetadataRepository beanMetadataRepository) {
        registerEnhancer(new DatabasePropertyConstraintEnhancer(beanMetadataRepository));
        registerEnhancer(new DatabaseGeneratedPropertyConstraintEnhancer());
    }

}
