package org.jarbframework.constraint.violation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jarbframework.constraint.AbstractEntity;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    private String name;
    private String active;
    
    User() {
    }
    
    public User(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setActive(String active) {
        this.active = active;
    }
    
    public String getActive() {
        return active;
    }

}
