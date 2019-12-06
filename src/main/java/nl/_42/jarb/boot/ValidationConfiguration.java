package nl._42.jarb.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ValidatorFactory;

@Configuration
public class ValidationConfiguration {

  private static final String VALIDATION_FACTORY = "javax.persistence.validation.factory";

  @Bean
  @ConditionalOnMissingBean(ValidatorFactory.class)
  public ValidatorFactory validatorFactory() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.setValidationMessageSource(validatorSource());
    return validator;
  }

  @Bean
  public MessageSource validatorSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    return messageSource;
  }

  @Bean
  public HibernatePropertiesCustomizer validatorHibernatePropertiesCustomizer(ValidatorFactory validatorFactory) {
    return (properties) -> properties.put(VALIDATION_FACTORY, validatorFactory);
  }

}
