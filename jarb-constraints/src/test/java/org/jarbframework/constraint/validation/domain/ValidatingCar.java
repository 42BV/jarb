package org.jarbframework.constraint.validation.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jarbframework.constraint.AbstractEntity;
import org.jarbframework.constraint.validation.DatabaseConstrained;

@Entity
@Table(name = "cars")
@DatabaseConstrained
public class ValidatingCar extends AbstractEntity {

    private String licenseNumber;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Person owner;
    
    private String active;

    private transient String unmappedProperty;

    ValidatingCar() {
    }
    
    public ValidatingCar(String licenseNumber) {
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

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getActive() {
        return active;
    }
    
    public void setActive(String active) {
        this.active = active;
    }
    
    public String getUnmappedProperty() {
        return unmappedProperty;
    }

    public void setUnmappedProperty(String unmappedProperty) {
        this.unmappedProperty = unmappedProperty;
    }

}
