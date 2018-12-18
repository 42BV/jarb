package nl._42.jarb.utils.orm.hibernate;

import static org.springframework.orm.jpa.EntityManagerFactoryUtils.ENTITY_MANAGER_SYNCHRONIZATION_ORDER;
import static org.springframework.util.ReflectionUtils.invokeMethod;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.StatelessSessionImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import nl._42.jarb.utils.Asserts;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * Hibernate's {@link StatelessSession} factory which will be bound to the current transaction.
 * This factory returns a Proxy which delegates method calls to the underlying {@link StatelessSession} bound to transaction.
 * At the end of the transaction the session is automatically closed. This class borrows idea's from {@link DataSourceUtils},
 * {@link EntityManagerFactoryUtils}, {@link ResourceHolderSynchronization} and {@link LocalEntityManagerFactoryBean}.
 *
 * @author Willem Dekker
 */
public class StatelessSessionFactoryBean implements FactoryBean<FlushableStatelessSession> {

    private final HibernateEntityManagerFactory entityManagerFactory;

    private SessionFactory sessionFactory;

    @Autowired
    public StatelessSessionFactoryBean(HibernateEntityManagerFactory entityManagerFactory) {
        super();
        this.entityManagerFactory = entityManagerFactory;
        this.sessionFactory = entityManagerFactory.getSessionFactory();
    }

    @Override
    public FlushableStatelessSession getObject() {
        Assert.notNull(entityManagerFactory, "Entity manager factory must not be null");
        Assert.notNull(sessionFactory, "Session factory must not be null");
        return ProxyFactory.getProxy(FlushableStatelessSession.class, new StatelessSessionInterceptor(entityManagerFactory, sessionFactory));
    }

    @Override
    public Class<?> getObjectType() {
        return FlushableStatelessSession.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Use this to override the {@link SessionFactory} obtained from the {@link EntityManagerFactory}.
     * Please note that the connection will still be used from the {@link EntityManager}.
     * @param sessionFactory the session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected static class StatelessSessionInterceptor implements MethodInterceptor {

        private static final String FLUSH_METHOD_NAME = "flush";

        private final EntityManagerFactory entityManagerFactory;

        private final SessionFactory sessionFactory;

        public StatelessSessionInterceptor(EntityManagerFactory entityManagerFactory, SessionFactory sessionFactory) {
            this.entityManagerFactory = entityManagerFactory;
            this.sessionFactory = sessionFactory;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            StatelessSession session = getCurrentSession();
            return doInvoke(invocation, session);
        }

        private Object doInvoke(MethodInvocation invocation, StatelessSession session) {
            Object result = null;
            if (isFlushCalled(invocation)) {
                flush(session);
            } else {
                result = invokeMethod(invocation.getMethod(), session, invocation.getArguments());
            }
            return result;
        }

        private boolean isFlushCalled(MethodInvocation invocation) {
            return FLUSH_METHOD_NAME.equals(invocation.getMethod().getName());
        }

        private static void flush(StatelessSession session) {
            if (session instanceof FlushableStatelessSession) {
                ((FlushableStatelessSession) session).flush();
            } else if (session instanceof StatelessSessionImpl) {
                ((StatelessSessionImpl) session).flush();
            }
        }

        private StatelessSession getCurrentSession() {
            Asserts.state(TransactionSynchronizationManager.isActualTransactionActive(), "There should be an active transaction for the current thread.");
            StatelessSession session = (StatelessSession) TransactionSynchronizationManager.getResource(sessionFactory);
            if (session == null) {
                session = openNewStatelessSession();
                bindWithTransaction(session);
            }
            return session;
        }

        private StatelessSession openNewStatelessSession() {
            return sessionFactory.openStatelessSession(getPhysicalConnection());
        }

        /**
         * It is important we obtain the physical (real) connection otherwise it will be double proxied and there will be problems releasing the connection.
         */
        private Connection getPhysicalConnection() {
            EntityManager entityManager = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
            SessionImplementor sessionImplementor = (SessionImplementor) entityManager.getDelegate();
            return sessionImplementor.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
        }

        private void bindWithTransaction(StatelessSession statelessSession) {
            TransactionSynchronizationManager.registerSynchronization(new StatelessSessionSynchronization(sessionFactory, statelessSession));
            TransactionSynchronizationManager.bindResource(sessionFactory, statelessSession);
        }

    }

    protected static final class StatelessSessionSynchronization extends TransactionSynchronizationAdapter {

        private static final int ARBITRARY_ORDER_DOWNER = 100;

        private static final int STATELESS_SESSION_SYNCHRONIZATION_ORDER = ENTITY_MANAGER_SYNCHRONIZATION_ORDER - ARBITRARY_ORDER_DOWNER;

        private final SessionFactory sessionFactory;

        private final StatelessSession statelessSession;

        public StatelessSessionSynchronization(SessionFactory sessionFactory, StatelessSession statelessSession) {
            this.sessionFactory = sessionFactory;
            this.statelessSession = statelessSession;
        }

        @Override
        public int getOrder() {
            return STATELESS_SESSION_SYNCHRONIZATION_ORDER;
        }

        @Override
        public void flush() {
            StatelessSessionInterceptor.flush(statelessSession);
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            if (!readOnly) {
                flush();
            }
        }

        @Override
        public void beforeCompletion() {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
            statelessSession.close();
        }

    }

}
