package domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class to test if many-to-many relations on child classes work.
 * @author Sander Benschop
 *
 */
@Entity
@Table(name = "gifts")
public class BusinessRelationshipGift {

    @Id
    @Column(name = "gift_id")
    @GeneratedValue
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "value")
    private Double value;

    /**
     * Returns the id.
     * @return Id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the description of the gift.
     * @param description Description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the description.
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value.
     * @param value Monetary value
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Returns the value.
     * @return Monetary value
     */
    public Double getValue() {
        return value;
    }

}
