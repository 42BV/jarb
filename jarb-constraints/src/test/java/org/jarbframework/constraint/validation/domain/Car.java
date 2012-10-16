package org.jarbframework.constraint.validation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jarbframework.constraint.AbstractEntity;
import org.jarbframework.constraint.validation.DatabaseConstrained;

@Entity
@Table(name = "cars")
@DatabaseConstrained
public class Car extends AbstractEntity {

    @Column(name = "license_number")
    private String licenseNumber;

    private Double price;

    @ManyToOne
    private Person owner;

    private transient String unmappedProperty;

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

    public String getUnmappedProperty() {
        return unmappedProperty;
    }

    public void setUnmappedProperty(String unmappedProperty) {
        this.unmappedProperty = unmappedProperty;
    }

}
