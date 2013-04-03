package org.jarbframework.constraint.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.jarbframework.constraint.AbstractEntity;

@Entity
@Table(name = "countries")
public class Country extends AbstractEntity {
    
    private String name;

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
