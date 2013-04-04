package org.jarbframework.constraint.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jarbframework.constraint.AbstractEntity;

@Entity
@Table(name = "cars")
public class Car extends AbstractEntity {

    private String licenseNumber;

    private Double price;

    private String active;

    Car() {
    }
    
    public Car(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getActive() {
        return active;
    }
    
    public void setActive(String active) {
        this.active = active;
    }

}
