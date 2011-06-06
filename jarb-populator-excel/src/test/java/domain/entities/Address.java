package domain.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Embeddable class containing an address (which has been renamed streetAndNumber to prevent confusion) and a city.
 * Is Embedded into Workspace.java
 * @author Sander Benschop
 * 
 */
@Embeddable
public final class Address {

    /** Constant for max length of address field.
     * Though here it's only used to check if the component skips static final ints. */
    public static final int MAX_ADDRESS_LENGTH = 64;

    @Column(name = "building_address")
    private String streetAndNumber;

    @Column(name = "city_name")
    private String city;

    /**
     * Construct a new {@link Address}.
     */
    public Address() {
        super();
    }

    /**
     * Construct a new {@link Address}.
     * @param streetAndNumber the street name and number
     * @param city the city it's in
     */
    public Address(String streetAndNumber, String city) {
        super();
        this.setStreetAndNumber(streetAndNumber);
        this.setCity(city);
    }

    /**
     * Sets the street name and number.
     * @param streetAndNumber Street name and number
     */
    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    /**
     * Return street name and number.
     * @return Street name and number
     */
    public String getStreetAndNumber() {
        return streetAndNumber;
    }

    /**
     * Sets the city name.
     * @param city The city's name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Return the city's name.
     * @return City's name
     */
    public String getCity() {
        return city;
    }

}
