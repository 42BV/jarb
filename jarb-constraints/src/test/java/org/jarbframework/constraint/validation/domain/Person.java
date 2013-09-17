package org.jarbframework.constraint.validation.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jarbframework.constraint.AbstractEntity;
import org.jarbframework.constraint.validation.DatabaseConstrained;

@Entity
@Table(name = "persons")
@DatabaseConstrained
public class Person extends AbstractEntity {

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
