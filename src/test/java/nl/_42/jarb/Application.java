package nl._42.jarb;

import nl._42.jarb.constraint.EnableDatabaseConstraints;
import nl._42.jarb.domain.Car;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = Car.class)
@EntityScan(basePackageClasses = Car.class)
@EnableDatabaseConstraints(basePackageClasses = Application.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
