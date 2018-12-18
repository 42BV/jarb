package nl._42.jarb.constraint.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "wines")
public class Wine extends DefaultEntity {

    @NotNull
    @NotEmpty
    @Length(min = 6)
    private String name;
    
    @Digits(integer = 5, fraction = 2)
    private Double price;
    
    @ManyToOne
    private Country country;
    
    private transient String unmappedProperty;
    
    private int primitive;

    @Length(min = 5)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Digits(integer = 6, fraction = 1)
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Country getCountry() {
        return country;
    }
    
    public void setCountry(Country country) {
        this.country = country;
    }
    
    public String getUnmappedProperty() {
        return unmappedProperty;
    }
    
    public void setUnmappedProperty(String unmappedProperty) {
        this.unmappedProperty = unmappedProperty;
    }
    
    public int getPrimitive() {
        return primitive;
    }
    
    public void setPrimitive(int primitive) {
        this.primitive = primitive;
    }
    
}
