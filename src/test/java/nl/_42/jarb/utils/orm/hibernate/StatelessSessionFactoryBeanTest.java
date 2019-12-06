package nl._42.jarb.utils.orm.hibernate;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.StatelessSessionImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StatelessSessionFactoryBeanTest {
    
    @Mock
    private HibernateEntityManagerFactory entityManagerFactory;

    @Mock
    private StatelessSessionImpl statelessSession;

    @Mock
    private SessionFactoryImplementor sessionFactory;

    private StatelessSessionFactoryBean sessionFactoryBean;

    @Before
    public void setUp() {
        Mockito.when(entityManagerFactory.getSessionFactory()).thenReturn(sessionFactory);
        sessionFactoryBean = new StatelessSessionFactoryBean(entityManagerFactory);
    }

    @Test
    public void testIsSingleton() {
        assertTrue(sessionFactoryBean.isSingleton());
    }

    @Test
    public void testGetObjectType() {
        assertEquals(FlushableStatelessSession.class, sessionFactoryBean.getObjectType());
    }

    @Test
    public void testGetObject() {
        sessionFactoryBean.setSessionFactory(sessionFactory);

        assertNotNull(sessionFactoryBean.getObject());
    }

    // Synchronization tests

    @Test
    public void testSynchronizationOrder() {
        StatelessSessionFactoryBean.StatelessSessionSynchronization synchronization = new StatelessSessionFactoryBean.StatelessSessionSynchronization(null, null);
        Assert.assertEquals(800, synchronization.getOrder());
    }

    @Test
    public void testSynchronizationFlush() {
        StatelessSessionFactoryBean.StatelessSessionSynchronization synchronization = new StatelessSessionFactoryBean.StatelessSessionSynchronization(null, statelessSession);
        synchronization.beforeCommit(false);

        Mockito.verify(statelessSession).flush();
    }

    @Test
    public void testSynchronizationFlushReadOnly() {
        StatelessSessionFactoryBean.StatelessSessionSynchronization synchronization = new StatelessSessionFactoryBean.StatelessSessionSynchronization(null, statelessSession);
        synchronization.beforeCommit(true);

        Mockito.verifyZeroInteractions(statelessSession);
    }

}
