package domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nl.mad.hactar.common.ReflectionUtil;

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
        ReflectionUtil.setFieldValue(release, "number", 3);
        assertEquals((Integer) 3, release.getNumber());
    }

    @Test
    public void testSetGetDate() {
        Date date = new Date();
        ReflectionUtil.setFieldValue(release, "releaseDate", date);
        assertEquals(date, release.getDate());
    }

    @Test
    public void testSetGetType() {
        ReflectionUtil.setFieldValue(release, "type", 'A');
        assertEquals((Character) 'A', release.getType());
    }
}
