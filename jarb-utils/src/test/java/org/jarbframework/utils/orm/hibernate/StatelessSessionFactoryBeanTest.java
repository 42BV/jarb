package org.jarbframework.utils.orm.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.hibernate.SessionFactory;
import org.hibernate.internal.StatelessSessionImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.jarbframework.utils.orm.hibernate.StatelessSessionFactoryBean.StatelessSessionSynchronization;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class StatelessSessionFactoryBeanTest {
    
    @Mocked
    private HibernateEntityManagerFactory entityManagerFactory;

    @Mocked
    private SessionFactory sessionFactory;

    private StatelessSessionFactoryBean sessionFactoryBean;

    @Before
    public void setUp() {
        new Expectations() {{
            entityManagerFactory.getSessionFactory();
            result = sessionFactory;
        }};

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
        StatelessSessionSynchronization synchronization = new StatelessSessionSynchronization(null, null);
        Assert.assertEquals(800, synchronization.getOrder());
    }

    @Test
    public void testSynchronizationFlush(@Mocked final StatelessSessionImpl statelessSession) {
        StatelessSessionSynchronization synchronization = new StatelessSessionSynchronization(null, statelessSession);
        synchronization.beforeCommit(false);

        new Verifications() {{
            statelessSession.managedFlush();
            times = 1;
        }};        
    }

    @Test
    public void testSynchronizationFlushReadOnly(@Mocked final StatelessSessionImpl statelessSession) {
        StatelessSessionSynchronization synchronization = new StatelessSessionSynchronization(null, statelessSession);
        synchronization.beforeCommit(true);

        new Verifications() {{
            statelessSession.managedFlush();
            times = 0;
        }}; 
    }

    @Test
    public void testIsFlushCalled(@Mocked final StatelessSessionImpl statelessSession, @Mocked final TransactionSynchronizationManager transactionSynchronizationManager) {
        new NonStrictExpectations() {{
            TransactionSynchronizationManager.isActualTransactionActive();
            result = true;
            
            TransactionSynchronizationManager.getResource(sessionFactory);
            result = statelessSession;
        }};

        sessionFactoryBean.setSessionFactory(sessionFactory);
        sessionFactoryBean.getObject().flush();

        new Verifications() {{
            statelessSession.managedFlush();
            times = 1;
        }}; 
    }

}
