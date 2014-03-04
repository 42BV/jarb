/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata;

import java.util.Date;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.URL;
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
import org.jarbframework.utils.Classes;

/**
 * Bean constraint descriptor with all default enhancers. 
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class DefaultBeanConstraintDescriptor extends BeanConstraintDescriptor {
    
    private static final String HIBERNATE_VALIDATOR_PACKAGE_NAME = "org.hibernate.validator";

    public DefaultBeanConstraintDescriptor(BeanMetadataRepository beanMetadataRepository) {
        registerDefaultEnhancers();
        if (Classes.hasPackage(HIBERNATE_VALIDATOR_PACKAGE_NAME)) {
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
    }
    
    private void registerHibernateEnhancers() {
        registerEnhancer(new LengthPropertyConstraintEnhancer());
        registerEnhancer(new DigitsPropertyConstraintEnhancer());
        registerEnhancer(new NotNullPropertyConstraintEnhancer());
        registerEnhancer(new NotEmptyPropertyConstraintEnhancer());
        registerEnhancer(new PatternPropertyConstraintEnhancer());
        
        registerEnhancer(new AnnotationPropertyTypeEnhancer(Email.class, "email"));
        registerEnhancer(new AnnotationPropertyTypeEnhancer(CreditCardNumber.class, "credid_card"));
        registerEnhancer(new AnnotationPropertyTypeEnhancer(URL.class, "url"));
    }
    
    private void registerDatabaseEnhancers(BeanMetadataRepository beanMetadataRepository) {
        registerEnhancer(new DatabasePropertyConstraintEnhancer(beanMetadataRepository));
        registerEnhancer(new DatabaseGeneratedPropertyConstraintEnhancer());
    }

}
