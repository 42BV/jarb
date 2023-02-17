package nl._42.jarb.autoconfigure;

import nl._42.jarb.constraint.metadata.BeanConstraintController;
import nl._42.jarb.constraint.metadata.BeanConstraintService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Autoconfig for JARB related beans
 */
@Configuration
public class JarbAutoConfig {

    private static final String VALIDATION_FACTORY = "jakarta.persistence.validation.factory";

    @Bean
    @ConditionalOnMissingBean(HibernatePropertiesCustomizer.class)
    public HibernatePropertiesCustomizer validatorHibernatePropertiesCustomizer(LocalValidatorFactoryBean validatorFactory) {
        return properties -> properties.put(VALIDATION_FACTORY, validatorFactory);
    }

    @Bean
    @ConditionalOnMissingBean(BeanConstraintController.class)
    public BeanConstraintController beanConstraintController(BeanConstraintService beanConstraintService) {
        return new BeanConstraintController(beanConstraintService);
    }

}
