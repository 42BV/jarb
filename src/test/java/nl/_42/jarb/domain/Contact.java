package nl._42.jarb.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

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
