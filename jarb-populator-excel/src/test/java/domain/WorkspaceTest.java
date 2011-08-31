package domain;

import static org.junit.Assert.assertEquals;

import org.jarb.utils.bean.ModifiableBean;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.Workspace;

public class WorkspaceTest {

    private Workspace workspace;

    @Before
    public void setupWorkpace() {
        workspace = new Workspace();
    }

    @Test
    public void testGetId() {
        final Long id = 1L;
        ModifiableBean.wrap(workspace).setPropertyValue("id", id);
        assertEquals(id, workspace.getId());
    }

    @Test
    public void testSetGetInvoiceAddress() {
        Address address = new Address("Eerste Stationsstraat 12", "Zoetermeer");
        workspace.setInvoiceAddress(address);
        assertEquals(address, workspace.getInvoiceAddress());
    }

    @Test
    public void testSetGetShippingAddress() {
        Address address = new Address("Burgemeester Jaworskistraat 31", "Zoetermeer");
        workspace.setShippingAddress(address);
        assertEquals(address, workspace.getShippingAddress());
    }

    @Test
    public void testSetGetFloorNumber() {
        Integer floorNumber = 2;
        workspace.setFloorNumber(floorNumber);
        assertEquals(floorNumber, workspace.getFloorNumber());
    }

    @Test
    public void testSetGetCubicalNumber() {
        Double cubicalNumber = 34.0;
        workspace.setCubicalNumber(cubicalNumber);
        assertEquals(cubicalNumber, workspace.getCubicalNumber());
    }
}
