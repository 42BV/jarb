package nl._42.jarb.constraint.domain;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Contact {

    @Embedded
    private Address address;

    public Contact() {
    }

    public Contact(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

}
