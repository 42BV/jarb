package nl._42.jarb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "street_and_number")
    private String streetAndNumber;
    
    private String city;

    public Address() {
    }

    public Address(String streetAndNumber, String city) {
        this.streetAndNumber = streetAndNumber;
        this.city = city;
    }

    public String getStreetAndNumber() {
        return streetAndNumber;
    }

    public String getCity() {
        return city;
    }

}
