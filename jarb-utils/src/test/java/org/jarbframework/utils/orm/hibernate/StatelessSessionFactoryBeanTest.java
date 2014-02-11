package org.jarbframework.utils.orm.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.internal.StatelessSessionImpl;
import org.jarbframework.utils.orm.hibernate.StatelessSessionFactoryBean.StatelessSessionSynchronization;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TransactionSynchronizationManager.class)
public class StatelessSessionFactoryBeanTest {

    private StatelessSessionFactoryBean sessionFactoryBean;

    @Before
    public void setUp() {
        HibernateEntityManagerFactory entityManagerFactory = Mockito.mock(HibernateEntityManagerFactory.class);
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
        SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
        sessionFactoryBean.setSessionFactory(sessionFactory);

        assertNotNull(sessionFactoryBean.getObject());
    }

    // Synchronization tests

    @Test
    public void testSynchronizationOrder() {
        StatelessSessionSynchronization synchronization = new StatelessSessionSynchronization(null, null);
        Assert.assertEquals(800, synchronization.getOrder());
    }

    @Test
    public void testSynchronizationFlush() {
        StatelessSessionImpl statelessSession = Mockito.mock(StatelessSessionImpl.class);
        StatelessSessionSynchronization synchronization = new StatelessSessionSynchronization(null, statelessSession);
        synchronization.beforeCommit(false);

        Mockito.verify(statelessSession).managedFlush();
    }

    @Test
    public void testSynchronizationFlushReadOnly() {
        StatelessSessionImpl statelessSession = Mockito.mock(StatelessSessionImpl.class);
        StatelessSessionSynchronization synchronization = new StatelessSessionSynchronization(null, statelessSession);
        synchronization.beforeCommit(true);

        Mockito.verifyZeroInteractions(statelessSession);
    }

    @Test
    public void testIsFlushCalled() {
        StatelessSessionImpl statelessSession = Mockito.mock(StatelessSessionImpl.class);
        PowerMockito.mockStatic(TransactionSynchronizationManager.class);
        PowerMockito.when(TransactionSynchronizationManager.isActualTransactionActive()).thenReturn(true);
        PowerMockito.when(TransactionSynchronizationManager.getResource(Mockito.any())).thenReturn(statelessSession);

        SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
        sessionFactoryBean.setSessionFactory(sessionFactory);
        sessionFactoryBean.getObject().flush();

        Mockito.verify(statelessSession).managedFlush();
    }

}
