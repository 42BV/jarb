package nl._42.jarb.autoconfigure;

import nl._42.jarb.constraint.metadata.BeanConstraintController;
import nl._42.jarb.constraint.metadata.BeanConstraintService;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Autoconfig for JARB related beans. Every part backs off when its
 * (optional) dependencies are not available, so having JARB on the
 * classpath without {@code @EnableDatabaseConstraints} is harmless.
 */
@AutoConfiguration
public class JarbAutoConfig {

    /**
     * Registers the Spring managed validator factory in Hibernate.
     * Only active when both Hibernate and Bean Validation are on the classpath.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = {
        "org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer",
        "org.springframework.validation.beanvalidation.LocalValidatorFactoryBean",
        "jakarta.validation.ValidatorFactory"
    })
    static class ValidationConfiguration {

        private static final String VALIDATION_FACTORY = "jakarta.persistence.validation.factory";

        @Bean
        @ConditionalOnMissingBean(HibernatePropertiesCustomizer.class)
        HibernatePropertiesCustomizer validatorHibernatePropertiesCustomizer(ObjectProvider<LocalValidatorFactoryBean> validatorFactory) {
            return properties -> validatorFactory.ifAvailable(factory -> properties.put(VALIDATION_FACTORY, factory));
        }

    }

    /**
     * Exposes the bean constraint metadata over REST. Only active when
     * Spring MVC is on the classpath and database constraints are enabled.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnBean(BeanConstraintService.class)
    static class ControllerConfiguration {

        @Bean
        @ConditionalOnMissingBean(BeanConstraintController.class)
        BeanConstraintController beanConstraintController(BeanConstraintService beanConstraintService) {
            return new BeanConstraintController(beanConstraintService);
        }

    }

}
