package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * CompanyCar is a class that tests inheritance from an abstract class which also inherits from an abstract class.
 * This is needed because the project I'm using to test this component requires it.
 * @author Sander Benschop
 *
 */
@Entity
@DiscriminatorValue("car")
public class CompanyCar extends CompanyVehicle {

    /** Standard constructor. */
    public CompanyCar() {
        super();
    }

    /**
     * CompanyCar's argumented constructor.
     * @param model Model of the car
     * @param price Price of the car
     * @param mileage Mileage in gallons per mile.
     * @param gearbox Gearbox can either be AUTOMATIC or MANUAL
     * @param airbags Whether or not this car has airbags
     */
    public CompanyCar(String model, Double price, Integer mileage, Gearbox gearbox, Boolean airbags) {
        super(model, price, mileage, gearbox);
        this.airbags = airbags;
    }

    @Column(name = "airbags")
    private Boolean airbags;

    /**
     * Sets airbags to true or false.
     * @param hasAirbags Boolean value true or false.
     */
    public void setAirbags(Boolean hasAirbags) {
        this.airbags = hasAirbags;
    }

    /**
     * Returns true if the car has airbags.
     * @return True or false.
     */
    public Boolean hasAirbags() {
        return airbags;
    }

}
