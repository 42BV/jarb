package org.jarbframework.validation.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "street_and_number")
    private String streetAndNumber;
    private String city;
    
    public String getStreetAndNumber() {
        return streetAndNumber;
    }
    
    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
}
