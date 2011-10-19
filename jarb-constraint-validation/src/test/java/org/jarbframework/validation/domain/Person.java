package org.jarbframework.validation.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jarbframework.validation.DatabaseConstrained;

@Entity
@Table(name = "persons")
@DatabaseConstrained
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String age;
    
    @Embedded
    private Contact contact;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
