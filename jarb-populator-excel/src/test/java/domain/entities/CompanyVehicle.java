package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * CompanyVehicle is an abstract class, which inherits from VersionSuperClass and is inherited by CompanyCar and CompanyMotorbike.
 * This class was added to test a situation  like this, as the testing project for this component requires it.
 * @author Sander Benschop
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "vehicles")
public abstract class CompanyVehicle extends VersionSuperclass {

    /**
     * Gearbox can either be MANUAL or AUTOMATIC.
     */
    public enum Gearbox {
        /** Manual gearbox in which you have to change gears yourself. */
        MANUAL,

        /** Automatic gearbox in which you don't.*/
        AUTOMATIC;
    };

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "model")
    private String model;

    @Column(name = "price")
    private Double price;

    @Column(name = "mileage")
    private Integer milesPerGallon;

    @Column(name = "gearbox")
    @Enumerated(EnumType.STRING)
    private Gearbox gearBox;

    /** Standard constructor. */
    CompanyVehicle() {
        super();
    }

    /**
     * Argumented constructor.
     * @param model Model of the vehicle
     * @param price Price of the vehicle
     * @param mileage Mileage in gallons per mile.
     * @param gearbox Gearbox can either be AUTOMATIC or MANUAL
     */
    public CompanyVehicle(String model, Double price, Integer mileage, Gearbox gearbox) {
        super();
        this.model = model;
        this.price = price;
        this.milesPerGallon = mileage;
        this.gearBox = gearbox;
    }

    /**
     * Sets the Vehicle's id.
     * @param id ID value
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the vehicle's id.
     * @return Vehicle id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the vehicle's model.
     * @param vehicleModel Vehicle's model
     */
    public void setModel(String vehicleModel) {
        this.model = vehicleModel;
    }

    /**
     * Returns the vehicle's model.
     * @return Vehicle's model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the vehicles mileage in miles per gallon.
     * @param milesPerGallon Set mileage.
     */
    public void setMilesPerGallon(Integer milesPerGallon) {
        this.milesPerGallon = milesPerGallon;
    }

    /**
     * Returns the vehicles mileage in miles per gallon.
     * @return Mileage
     */
    public Integer getMilesPerGallon() {
        return milesPerGallon;
    }

    /**
     * Sets the vehicle's gearbox to MANUAL or AUTOMATIC.
     * @param gearBox Type of gearbox
     */
    public void setGearBox(Gearbox gearBox) {
        this.gearBox = gearBox;
    }

    /**
     * Returns the type of gearbox.
     * @return MANUAL or AUTOMATIC
     */
    public Gearbox getGearBox() {
        return gearBox;
    }

    /**
     * Sets the vehicle's price.
     * @param price Price of the vehicle
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Returns the vehicle's price.
     * @return Vehicle's price
     */
    public Double getPrice() {
        return price;
    }

}
