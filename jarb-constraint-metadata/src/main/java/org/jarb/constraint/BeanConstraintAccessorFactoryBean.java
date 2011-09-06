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
        BeanConstraintAccessorImpl accessor = new BeanConstraintAccessorImpl();
        accessor.registerEnhancer(new DatabasePropertyConstraintDescriptionEnhancer(databaseConstraintRepository));
        accessor.registerEnhancer(new AutoIncrementalPropertyEnhancer());
        // Basic constraint annotations
        accessor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        accessor.registerEnhancer(new DigitsPropertyConstraintEnhancer());
        accessor.registerEnhancer(new NotNullPropertyConstraintEnhancer());
        accessor.registerEnhancer(new NotEmptyPropertyConstraintEnhancer());
        // Type recognition
        accessor.registerEnhancer(new BasicTypesPropertyConstraintEnhancer());
        accessor.registerEnhancer(new AnnotationTypePropertyConstraintEnhancer(Email.class, "email"));
        accessor.registerEnhancer(new AnnotationTypePropertyConstraintEnhancer(CreditCardNumber.class, "credid_card"));
        return accessor;
    }

}
