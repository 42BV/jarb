package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.OtherDepartmentSubclass;

public class OtherDepartmentSubclassTest {

    private OtherDepartmentSubclass otherDepartmentSubclass;

    @Before
    public void setupOtherDepartmentSubclassTest() {
        otherDepartmentSubclass = new OtherDepartmentSubclass("Sales", "Jim de Vries", "Peter Jacobs");
    }

    @Test
    public void testSetGetDepartmentHead() {
        otherDepartmentSubclass.setDepartmentHead("Frank D'Amico");
        assertEquals("Frank D'Amico", otherDepartmentSubclass.getDepartmentHead());
    }

    @Test
    public void testSetGetSecondInCommand() {
        otherDepartmentSubclass.setSecondInCommand("Wallace Wells");
        assertEquals("Wallace Wells", otherDepartmentSubclass.getSecondInCommand());
    }

}
