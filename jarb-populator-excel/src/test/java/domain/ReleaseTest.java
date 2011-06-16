package domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jarb.populator.excel.util.ReflectionUtils;
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
        ReflectionUtils.setFieldValue(release, "number", 3);
        assertEquals((Integer) 3, release.getNumber());
    }

    @Test
    public void testSetGetDate() {
        Date date = new Date();
        ReflectionUtils.setFieldValue(release, "releaseDate", date);
        assertEquals(date, release.getDate());
    }

    @Test
    public void testSetGetType() {
        ReflectionUtils.setFieldValue(release, "type", 'A');
        assertEquals((Character) 'A', release.getType());
    }
}
