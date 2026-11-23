package nl._42.jarb.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import nl._42.jarb.constraint.metadata.BeanConstraintController;
import nl._42.jarb.constraint.metadata.BeanConstraintService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import jakarta.validation.ValidatorFactory;

/**
 * Verifies that {@link JarbAutoConfig} backs off when JARB is not enabled
 * or optional dependencies are missing (see issue #79).
 */
class JarbAutoConfigBackOffTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(JarbAutoConfig.class));

    @Test
    void withoutBeanConstraintService_shouldNotRegisterController() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(BeanConstraintController.class);
        });
    }

    @Test
    void withBeanConstraintService_shouldRegisterController() {
        runner.withBean(BeanConstraintService.class, () -> mock(BeanConstraintService.class))
              .run(context -> {
                  assertThat(context).hasNotFailed();
                  assertThat(context).hasSingleBean(BeanConstraintController.class);
              });
    }

    @Test
    void withoutValidationOnClasspath_shouldStartWithoutCustomizer() {
        runner.withClassLoader(new FilteredClassLoader(ValidatorFactory.class))
              .run(context -> {
                  assertThat(context).hasNotFailed();
                  assertThat(context).doesNotHaveBean(HibernatePropertiesCustomizer.class);
              });
    }

    @Test
    void withoutValidatorBean_shouldStillStart() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(HibernatePropertiesCustomizer.class);
        });
    }

}
