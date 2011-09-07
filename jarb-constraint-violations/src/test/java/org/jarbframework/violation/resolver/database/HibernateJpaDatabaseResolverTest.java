package org.jarbframework.violation.resolver.database;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManagerFactory;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.jarbframework.violation.resolver.database.DatabaseType;
import org.jarbframework.violation.resolver.database.HibernateDialectDatabaseTypeResolver;
import org.jarbframework.violation.resolver.database.HibernateJpaDatabaseTypeResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:hsql-context.xml" })
public class HibernateJpaDatabaseResolverTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void testResolveDatabase() {
        assertEquals(DatabaseType.HSQL, new HibernateJpaDatabaseTypeResolver(entityManagerFactory).resolve());
    }

    @Test
    public void testResolveDatabaseByInheritance() {
        assertEquals(DatabaseType.HSQL, new HibernateDialectDatabaseTypeResolver(MyHsqlDialect.class).resolve());
    }

    @Test(expected = IllegalStateException.class)
    public void testUnsupportedDialect() {
        new HibernateDialectDatabaseTypeResolver(CustomUnmappedDialect.class).resolve();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownDialectClass() {
        HibernateDialectDatabaseTypeResolver.forName("some.unknown.DialectClass");
    }

    public static class MyHsqlDialect extends HSQLDialect {
    }

    public static class CustomUnmappedDialect extends Dialect {
    }

}
