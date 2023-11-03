package nl._42.jarb.constraint.violation;

import nl._42.jarb.Application;
import nl._42.jarb.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest(classes = Application.class)
class ConcurrentResolverTest {

    static final int THREAD_COUNT = 100;

    @Autowired
    private DatabaseConstraintViolationResolver resolver;

    @Test
    void concurrentShouldSucceed() {
        ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            Future<Void> future = service.submit(() -> {
                Throwable exception = new SQLException(
                        "integrity constraint violation: NOT NULL check constraint ; SYS_CT_10156 table: CARS column: LICENSE_NUMBER");
                DatabaseConstraintViolation violation = resolver.resolve(exception);
                Assertions.assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
                return null;
            });
            futures.add(future);
        }

        futures.forEach(future -> Assertions.assertDoesNotThrow(() -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Test was interrupted", e);
            }
        }));

        service.shutdown();
    }

}
