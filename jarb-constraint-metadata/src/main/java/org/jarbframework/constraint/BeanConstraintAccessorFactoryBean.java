package org.jarbframework.constraint;

import java.util.Date;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.jarbframework.constraint.database.DatabaseConstraintRepository;
import org.jarbframework.constraint.database.DatabasePropertyConstraintDescriptionEnhancer;
import org.jarbframework.constraint.jsr303.DigitsPropertyConstraintEnhancer;
import org.jarbframework.constraint.jsr303.LengthPropertyConstraintEnhancer;
import org.jarbframework.constraint.jsr303.NotEmptyPropertyConstraintEnhancer;
import org.jarbframework.constraint.jsr303.NotNullPropertyConstraintEnhancer;
import org.jarbframework.utils.spring.SingletonFactoryBean;

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
        accessor.registerEnhancer(new AutoIncrementalPropertyConstraintEnhancer());
        // Basic constraint annotations
        accessor.registerEnhancer(new LengthPropertyConstraintEnhancer());
        accessor.registerEnhancer(new DigitsPropertyConstraintEnhancer());
        accessor.registerEnhancer(new NotNullPropertyConstraintEnhancer());
        accessor.registerEnhancer(new NotEmptyPropertyConstraintEnhancer());
        // Type recognition
        accessor.registerEnhancer(new ClassPropertyTypeEnhancer(String.class, "text"));
        accessor.registerEnhancer(new ClassPropertyTypeEnhancer(Date.class, "date"));
        accessor.registerEnhancer(new ClassPropertyTypeEnhancer(Number.class, "number"));
        accessor.registerEnhancer(new AnnotationPropertyTypeEnhancer(Email.class, "email"));
        accessor.registerEnhancer(new AnnotationPropertyTypeEnhancer(CreditCardNumber.class, "credid_card"));
        return accessor;
    }

}
