package nl._42.jarb.constraint.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import nl._42.jarb.constraint.validation.DatabaseConstrained;
import nl._42.jarb.constraint.validation.IgnoreDatabaseConstraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "cars")
@DatabaseConstrained
public class AwesomeCar extends DefaultEntity {

    private String licenseNumber;

    private Double price;

    @ManyToOne
    private Person owner;
    
    private String active;

    private transient String unmappedProperty;
    
    @IgnoreDatabaseConstraints
    private transient String ignoredProperty;

    @ElementCollection
    @CollectionTable(name = "cars_inspections", joinColumns = @JoinColumn(name = "cars_id"))
    private List<Inspection> inspections = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "cars_components", joinColumns = @JoinColumn(name = "cars_id"))
    @MapKeyColumn(name = "type")
    @MapKeyClass(ComponentType.class)
    @MapKeyEnumerated(EnumType.STRING)
    private Map<ComponentType, Component> components = new HashMap<>();

    AwesomeCar() {
    }
    
    public AwesomeCar(String licenseNumber) {
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

    public String getIgnoredProperty() {
        return ignoredProperty;
    }
    
    public void setIgnoredProperty(String ignoredProperty) {
        this.ignoredProperty = ignoredProperty;
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public void setInspections(List<Inspection> inspections) {
        this.inspections = inspections;
    }

    public Map<ComponentType, Component> getComponents() {
        return components;
    }

    public void setComponents(Map<ComponentType, Component> components) {
        this.components = components;
    }

}
