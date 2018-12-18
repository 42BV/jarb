package nl._42.jarb.utils.orm.hibernate;

import static org.junit.Assert.assertTrue;

import java.sql.Types;

import nl._42.jarb.utils.orm.hibernate.dialect.ImprovedHsqlDialect;

import org.hibernate.dialect.Dialect;
import nl._42.jarb.utils.orm.hibernate.dialect.ImprovedHsqlDialect;
import org.junit.Test;

public class ImprovedHsqlDialectTest {

    @Test
    public void testRegisterTypes() {
        Dialect dialect = new ImprovedHsqlDialect();
        assertTrue("boolean".equals(dialect.getTypeName(Types.BIT)));
        assertTrue("decimal($p,$s)".equals(dialect.getTypeName(Types.NUMERIC)));
    }

}
