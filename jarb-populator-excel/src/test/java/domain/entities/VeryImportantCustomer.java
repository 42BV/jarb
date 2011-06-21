package domain.entities;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * For testing inheritance upon inheritance.
 * @author Sander Benschop
 *
 */
@Entity
@DiscriminatorValue("vip")
public class VeryImportantCustomer extends SpecialCustomer {

    @Column(name = "official_title")
    private String officialTitle;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "vipcustomers_gifts", joinColumns = { @JoinColumn(name = "customer_id") }, inverseJoinColumns = { @JoinColumn(name = "gift_id") })
    private Set<BusinessRelationshipGift> gifts = new LinkedHashSet<BusinessRelationshipGift>();

    /**
     * Sets the title.
     * @param title Title
     */
    public void setTitle(String title) {
        this.officialTitle = title;
    }

    /**
     * Returns the title.
     * @return Title
     */
    public String getTitle() {
        return officialTitle;
    }

    public void setGifts(Set<BusinessRelationshipGift> gifts) {
        this.gifts = gifts;
    }

    public Set<BusinessRelationshipGift> getGifts() {
        return gifts;
    }
    
    public void addGift(BusinessRelationshipGift gift) {
        gifts.add(gift);
    }
}
