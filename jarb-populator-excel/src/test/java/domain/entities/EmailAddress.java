package domain.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * EmailAddress is an Embeddable used to test ElementCollections without JoinColumn annotations 
 * 
 * @author Sander Benschop
 * @author Pim van Dongen
 *
 */
@Embeddable
public class EmailAddress {

    @Column(name = "local_address")
    private String localAddress;

    @Column(name = "domain_address")
    private String domainAddress;

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setDomainAddress(String domainAddress) {
        this.domainAddress = domainAddress;
    }

    public String getDomainAddress() {
        return domainAddress;
    }

}
