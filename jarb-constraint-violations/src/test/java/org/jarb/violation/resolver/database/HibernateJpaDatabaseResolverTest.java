package org.jarb.violation.resolver.database;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManagerFactory;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
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
        assertEquals(Database.HSQL, new HibernateJpaDatabaseResolver(entityManagerFactory).resolve());
    }

    @Test
    public void testResolveDatabaseByInheritance() {
        assertEquals(Database.HSQL, HibernateDialectDatabaseResolver.resolveByDialect(MyHsqlDialect.class.getName()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedDialect() {
        HibernateDialectDatabaseResolver.resolveByDialect(CustomUnmappedDialect.class.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownDialectClass() {
        HibernateDialectDatabaseResolver.resolveByDialect("some.unknown.DialectClass");
    }

    public static class MyHsqlDialect extends HSQLDialect {
        // No implementation
    }

    public static class CustomUnmappedDialect extends Dialect {
        // No implementation
    }

}
