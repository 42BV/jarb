package domain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A class with projects. Holds an enumeration and boolean to test if this works.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
@Entity
@Table(name = "projects")
public final class Project {

    /** Enumeration with project types in it. */
    public enum Type {

        /** A development project. */
        DEVELOPMENT {
            /**
             * Just testing an override.
             */
            @Override
            public void test() {
                System.out.println("");
            }
        },

        /** An integration project. */
        INTEGRATION,

        /** A support project. */
        SUPPORT;

        /**
         * Just for testing too.
         */
        public void test() {
            //Just for testing!
        }
    };

    @Id
    @Column(name = "project_id")
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startdate")
    private Date startDate;

    @Column(name = "active_project")
    private Boolean activeProject;

    @Column(name = "type", nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer", nullable = true)
    private Customer customer;

    /** 
     * Public constructor.
     */
    public Project() {
    }

    /**
     * Returns the Project's id.
     * @return id number
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the Project's name.
     * @param name Project's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the Project's name.
     * @return Project's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Project's startdate.
     * @param startdate Starting date of project
     */
    public void setStartdate(Date startdate) {
        this.startDate = new Date(startdate.getTime());
    }

    /**
     * Returns the Project's startdate.
     * @return Starting date of project
     */
    public Date getStartdate() {
        return new Date(startDate.getTime());
    }

    /**
     * Sets the Project's customer.
     * @param customer Instance of Customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Returns the Project's customer.
     * @return Instance of Customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets whether the project is active or not.
     * @param isActiveProject True or false
     */
    public void setActiveProject(Boolean isActiveProject) {
        this.activeProject = isActiveProject;
    }

    /**
     * Returns whether the project is active or not.
     * @return True or false
     */
    public Boolean getActiveProject() {
        return activeProject;
    }

    /**
     * Sets the project's type.
     * @param type Type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the project's type.
     * @return Project type
     */
    public Type getType() {
        return type;
    }
}
