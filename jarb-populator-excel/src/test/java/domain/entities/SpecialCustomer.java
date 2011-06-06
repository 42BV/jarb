package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Special customer, used to test if inheritance works.
 * @author Sander Benschop
 *
 */
@Entity
@DiscriminatorValue("special")
public class SpecialCustomer extends Customer {

    @Column(name = "company_location")
    private String location;

    /**
     * Sets the special customer's location.
     * @param location Location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the special customer's location.
     * @return Special customer's location
     */
    public String getLocation() {
        return location;
    }

}
