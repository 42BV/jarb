package domain.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapping of the Workspace class.
 * @author Sander Benschop
 *
 */
@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @Column(name = "workspace_id")
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "streetAndNumber", column = @Column(name = "\"invoice.building_address\"")),
            @AttributeOverride(name = "city", column = @Column(name = "\"invoice.city_name\"")) })
    private Address invoice = new Address();

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "streetAndNumber", column = @Column(name = "\"shipping.building_address\"")),
            @AttributeOverride(name = "city", column = @Column(name = "\"shipping.city_name\"")) })
    private Address shipping = new Address();

    @Column(name = "floor")
    private Integer floorNumber;

    @Column(name = "cubical")
    private Double cubicalNumber;

    /**
     * Public constructor.
     */
    public Workspace() {
    }

    /**
     * Get the workspace id.
     * @return id number
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the workspace's floor number.
     * @return floor number
     */
    public Integer getFloorNumber() {
        return floorNumber;
    }

    /**
     * Sets the workspace's floor number.
     * @param floorNumber floor number to be set to workspace
     */
    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    /**
     * Returns the workspace's cubical number.
     * @return cubical number
     */
    public Double getCubicalNumber() {
        return cubicalNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the workspace's cubical number.
     * @param cubical cubical number to be set to workspace
     */
    public void setCubicalNumber(double cubical) {
        this.cubicalNumber = cubical;
    }

    /**
     * Set the invoiceAddress.
     * @param invoiceAddress InvoiceAddress object
     */
    public void setInvoiceAddress(Address invoiceAddress) {
        this.invoice = invoiceAddress;
    }

    /**
     * Return the invoiceAddress.
     * @return invoiceAddress object
     */
    public Address getInvoiceAddress() {
        return invoice;
    }

    /**
     * Set the shippingAddress.
     * @param shippingAddress ShippingAddress object
     */
    public void setShippingAddress(Address shippingAddress) {
        this.shipping = shippingAddress;
    }

    /**
     * Return the shippingAddress.
     * @return shippingAddress object
     */
    public Address getShippingAddress() {
        return shipping;
    }
}
