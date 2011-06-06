package domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class is part of a circular reference between two regular classes (Customer and  ServiceLevelAgreement).
 * This class is here to test if the cascading and circular referencing problems are solved. 
 * @author Sander Benschop
 *
 */
@Entity
@Table(name = "sla")
public class ServiceLevelAgreement {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "customer")
    private Customer customer;

    /**
     * Returns the SLA's id.
     * @return SLA's id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the title of the SLA.
     * @param title SLA title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the SLA's title.
     * @return SLA title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the customer.
     * @param customer Customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Returns the customer.
     * @return Customer
     */
    public Customer getCustomer() {
        return customer;
    }

}
