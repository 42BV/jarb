package domain.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Phone table was created for testing ElementCollection annotation.
 * It's embedded in Employee entity class.
 * @author Sander Benschop
 *
 */
@Embeddable
public final class Phone {

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_model")
    private String phoneModel;

    /**
     * Returns the phone number.
     * @return phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     * @param phoneNumber Phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the phone model.
     * @return Phone model
     */
    public String getPhoneModel() {
        return phoneModel;
    }

    /**
     * Sets the phone model.
     * @param phoneModel Phone model
     */
    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

}
