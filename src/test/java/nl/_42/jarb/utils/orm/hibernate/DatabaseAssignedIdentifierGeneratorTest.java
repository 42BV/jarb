package nl._42.jarb.utils.orm.hibernate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl._42.jarb.utils.orm.hibernate.dialect.ImprovedHsqlDialect;

import org.hibernate.HibernateException;
import org.hibernate.id.PostInsertIdentityPersister;
import nl._42.jarb.utils.orm.hibernate.dialect.ImprovedHsqlDialect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseAssignedIdentifierGeneratorTest {

    @Mock
    private PostInsertIdentityPersister persister;

    @Test
    public void testValidNumberOfKeyColumns() {
        when(persister.getRootTableKeyColumnNames()).thenReturn(new String[] { "firstId" });

        new DatabaseAssignedIdentifierGenerator().getInsertGeneratedIdentifierDelegate(persister, new ImprovedHsqlDialect(), true);

        verify(persister).getRootTableKeyColumnNames();
    }

    @Test(expected = HibernateException.class)
    public void testInvalidNumberOfKeyColumns() {
        when(persister.getRootTableKeyColumnNames()).thenReturn(new String[] { "firstId", "secondId" });

        new DatabaseAssignedIdentifierGenerator().getInsertGeneratedIdentifierDelegate(persister, new ImprovedHsqlDialect(), true);
    }

}
