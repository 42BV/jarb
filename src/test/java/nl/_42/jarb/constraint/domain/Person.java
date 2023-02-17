package nl._42.jarb.constraint.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import nl._42.jarb.constraint.validation.DatabaseConstrained;

@Entity
@Table(name = "persons")
@DatabaseConstrained
public class Person extends DefaultEntity {

    private String name;
    
    private String age;
    
    @Embedded
    private Contact contact;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
    
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
