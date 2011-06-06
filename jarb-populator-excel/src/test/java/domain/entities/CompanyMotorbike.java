package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * CompanyMotorBike is a class that tests inheritance from an abstract class which also inherits from an abstract class.
 * This is needed because the project I'm using to test this component requires it.
 * @author Sander Benschop
 *
 */
@Entity
@DiscriminatorValue("bike")
public class CompanyMotorbike extends CompanyVehicle {

    /** Standard constructor. */
    public CompanyMotorbike() {
        super();
    }

    /**
     * CompanyMotorbike's argumented constructor.
     * @param model Model of the bike
     * @param price Price of the bike
     * @param mileage Mileage in gallons per mile.
     * @param gearbox Gearbox can either be AUTOMATIC or MANUAL
     * @param hasKickstarter Whether or not this bike has a kickstarter
     */
    public CompanyMotorbike(String model, Double price, Integer mileage, Gearbox gearbox, Boolean hasKickstarter) {
        super(model, price, mileage, gearbox);
        this.kickstarter = hasKickstarter;
    }

    @Column(name = "kickstarter")
    private Boolean kickstarter;

    /**
     * Set to true if the bike has a kickstarter.
     * @param hasKickstarter Boolean value true or false.
     */
    public void setKickstarter(Boolean hasKickstarter) {
        this.kickstarter = hasKickstarter;
    }

    /**
     * Returns true if the bike has a kickstarter.
     * @return True or false.
     */
    public Boolean hasKickstarter() {
        return kickstarter;
    }

}
