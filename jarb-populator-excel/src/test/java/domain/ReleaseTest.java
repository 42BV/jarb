package domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jarb.utils.bean.ModifiableBean;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Release;

public class ReleaseTest {

    public Release release;

    @Before
    public void setupReleaseTest() {
        release = new Release();
    }

    @Test
    public void testSetGetNumber() {
        new ModifiableBean<Release>(release).setPropertyValue("number", 3);
        assertEquals((Integer) 3, release.getNumber());
    }

    @Test
    public void testSetGetDate() {
        Date date = new Date();
        new ModifiableBean<Release>(release).setPropertyValue("releaseDate", date);
        assertEquals(date, release.getDate());
    }

    @Test
    public void testSetGetType() {
        new ModifiableBean<Release>(release).setPropertyValue("type", 'A');
        assertEquals((Character) 'A', release.getType());
    }
}
