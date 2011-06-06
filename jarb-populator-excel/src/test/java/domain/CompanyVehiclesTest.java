package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import domain.entities.CompanyCar;
import domain.entities.CompanyMotorbike;
import domain.entities.CompanyVehicle.Gearbox;

public class CompanyVehiclesTest {

    private CompanyCar car;

    @Before
    public void prepareCompanyCarTest() {
        car = new CompanyCar();
    }

    @Test
    public void testSetGetId() {
        Long id = (long) 13;
        car.setId(id);
        assertEquals(id, car.getId());
    }

    @Test
    public void testSetGetGearbox() {
        car.setGearBox(Gearbox.MANUAL);
        assertEquals(Gearbox.MANUAL, car.getGearBox());
    }

    @Test
    public void testSetGetMilesPerGallon() {
        car.setMilesPerGallon(40);
        assertEquals((Integer) 40, car.getMilesPerGallon());
    }

    @Test
    public void testSetGetModel() {
        car.setModel("Audi A6 Sedan");
        assertEquals("Audi A6 Sedan", car.getModel());
    }

    @Test
    public void testSetGetPrice() {
        car.setPrice(50000.00);
        assertEquals((Double) 50000.00, car.getPrice());
    }

    @Test
    public void testSetGetHasAirbags() {
        car.setAirbags(true);
        assertTrue(car.hasAirbags());
    }

    @Test
    public void testGetVersion() {
        car.getVersion();
    }

    @Test
    public void testSetGetHasKickstarter() {
        CompanyMotorbike bike = new CompanyMotorbike();
        bike.setKickstarter(true);
        assertTrue(bike.hasKickstarter());
    }

    @Test
    public void testArgumentedBikeConstructor() {
        CompanyMotorbike bike = new CompanyMotorbike("Harley Davidson", 20000.00, 70, Gearbox.MANUAL, true);
        assertEquals("Harley Davidson", bike.getModel());
    }
}
