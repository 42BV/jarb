package org.jarb.constraint;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.jarb.constraint.database.DatabaseConstraintRepository;
import org.jarb.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarb.constraint.jsr303.AnnotationTypePropertyConstraintEnhancer;
import org.jarb.constraint.jsr303.DigitsPropertyConstraintEnhancer;
import org.jarb.constraint.jsr303.LengthPropertyConstraintEnhancer;
import org.jarb.constraint.jsr303.NotEmptyPropertyConstraintEnhancer;
import org.jarb.constraint.jsr303.NotNullPropertyConstraintEnhancer;
import org.jarb.utils.spring.SingletonFactoryBean;

/**
 * Builds a default bean constraint metadata generator.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 */
public class BeanConstraintAccessorFactoryBean extends SingletonFactoryBean<BeanConstraintAccessor> {
    private DatabaseConstraintRepository databaseConstraintRepository;

    public void setDatabaseConstraintRepository(DatabaseConstraintRepository databaseConstraintRepository) {
        this.databaseConstraintRepository = databaseConstraintRepository;
    }

    @Override
    protected BeanConstraintAccessor createObject() throws Exception {
        BeanConstraintAccessorImpl descriptor = new BeanConstraintAccessorImpl();
        descriptor.registerEnhancer(new DatabasePropertyConstraintDescriptionEnhancer(databaseConstraintRepository));
        descriptor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        descriptor.registerEnhancer(new DigitsPropertyConstraintEnhancer());
        descriptor.registerEnhancer(new NotNullPropertyConstraintEnhancer());
        descriptor.registerEnhancer(new NotEmptyPropertyConstraintEnhancer());
        // Property type recognition
        descriptor.registerEnhancer(new BasicTypesPropertyConstraintEnhancer());
        descriptor.registerEnhancer(new AnnotationTypePropertyConstraintEnhancer(CreditCardNumber.class, "credid_card"));
        descriptor.registerEnhancer(new AnnotationTypePropertyConstraintEnhancer(Email.class, "email"));
        return descriptor;
    }

}
