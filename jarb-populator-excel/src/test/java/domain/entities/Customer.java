package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Mapping for Customer table.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
//This is for testing @Entity name annotations.
@Entity(name = "customers")
@DiscriminatorColumn(name = "type")
public class Customer {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue
    private Long id;

    @Column(name = "first_name")
    private String name;

    @Column(name = "company_name", nullable = true)
    private String companyName;

    @ManyToOne
    @JoinColumn(name = "service_level_agreement")
    private ServiceLevelAgreement serviceLevelAgreement;

    /**
     * Public constructor.
     */
    public Customer() {
    }

    /**
     * Get the customer's id.
     * @return id number
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the customer's name.
     * @param name Name to be set to customer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the customer's name.
     * @return Customer's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets customer's company name.
     * @param companyName Name of the customer's company
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Returns the customer's company name.
     * @return Name of the customers company
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Adds a service level agreement. 
     * @param serviceLevelAgreement SLA
     */
    public void setServiceLevelAgreement(ServiceLevelAgreement serviceLevelAgreement) {
        this.serviceLevelAgreement = serviceLevelAgreement;
    }

    /**
     * Returns a service level agreement.
     * @return SLA
     */
    public ServiceLevelAgreement getServiceLevelAgreement() {
        return serviceLevelAgreement;
    }
}
