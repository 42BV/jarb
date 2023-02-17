package nl._42.jarb.autoconfigure;

import nl._42.jarb.constraint.metadata.BeanConstraintController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JarbAutoConfigureTest {

    @Autowired
    private BeanConstraintController controller;

    @Test
    public void beanConstraintController_shouldExistInContext() {
        assertNotNull(controller);
    }

}
