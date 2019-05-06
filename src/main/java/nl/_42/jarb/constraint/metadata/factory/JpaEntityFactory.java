package nl._42.jarb.constraint.metadata.factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaEntityFactory implements EntityFactory {

  private final EntityManagerFactory entityManagerFactory;

  public JpaEntityFactory(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Override
  public Set<Class<?>> getEntityClasses() {
    Metamodel metamodel = entityManagerFactory.getMetamodel();
    return metamodel.getEntities().stream()
                    .map(entity -> entity.getJavaType())
                    .collect(Collectors.toSet());
  }

}
