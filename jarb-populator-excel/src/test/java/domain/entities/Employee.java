package domain.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Mapping of the employees class.
 * @author Willem Eppen
 * @author Sander Benschop
 * 
 */
@Entity
@Table(name = "employees")
public final class Employee {

    @Id
    @Column(name = "employee_id")
    @GeneratedValue
    private Integer id;

    @Column(name = "first_name")
    private String name;

    @Column(name = "salary_month")
    private Double salary;

    @Column(name = "salary_scale")
    private Character salaryScale;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Embedded
    private Address address = new Address();

    @ManyToOne
    @JoinColumn(name = "company_vehicle_id")
    private CompanyVehicle vehicle;

    @ManyToMany
    @JoinTable(name = "employees_projects", joinColumns = { @JoinColumn(name = "employee_id") }, inverseJoinColumns = { @JoinColumn(name = "project_id") })
    /** Set of projects these employees work on */
    private Set<Project> projects;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "phones", joinColumns = @JoinColumn(name = "owner"))
    private List<Phone> phones;
    
    @ElementCollection
    private List<EmailAddress> emailAddresses;

    /**
     * Public constructor, initializes projects hashmap.
     */
    public Employee() {
        projects = new HashSet<Project>();
    }

    /**
     * Returns the employee's id.
     * @return id number
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the employee's name.
     * @param name Employee's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the employee's name.
     * @return Employee's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the employee's salary.
     * @param salary amount to be set
     */
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    /**
     * Returns the employee's salary.
     * @return Employee's salary
     */
    public Double getSalary() {
        return salary;
    }

    /**
     * Returns the Employee's projects.
     * @return Set of projects
     */
    public Set<Project> getProjects() {
        return projects;
    }

    /**
     * Sets the Employee's projects.
     * @param projects Set of project's
     */
    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    /**
     * Sets the Employee's home address.
     * @param homeAddress Employee's home address
     */
    public void setAddress(Address homeAddress) {
        this.address = homeAddress;
    }

    /**
     * Returns the Employee's home address.
     * @return Employee's home address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the date of birth.
     * @param birthDate Date of birth.
     */
    public void setDateOfBirth(Date birthDate) {
        this.dateOfBirth = new Date(birthDate.getTime());
    }

    /**
     * Returns the date of birth.
     * @return Date of birth.
     */
    public Date getDateOfBirth() {
        if (dateOfBirth == null) return null;
        return new Date(dateOfBirth.getTime());
    }

    /**
     * Sets the employee's salary scale.
     * @param salaryScale Employee's salary scale
     */
    public void setSalaryScale(Character salaryScale) {
        this.salaryScale = salaryScale;
    }

    /**
     * Returns the employee's salary scale.
     * @return Employee's salary scale
     */
    public Character getSalaryScale() {
        return salaryScale;
    }

    /**
     * Sets the employee's company vehicle.
     * @param vehicle Car or bike
     */
    public void setVehicle(CompanyVehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Returns the employee's company vehicle.
     * @return Company car or bike
     */
    public CompanyVehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the Employee's phones
     * @return Employee's phones
     */
    public List<Phone> getPhones() {
        return phones;
    }

    /**
     * Sets the Employee's phones
     * @param phones Employee's phones
     */
    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    /**
     * Returns the Employee's collection of emailAddresses.
     * @return Employee's email adresses.
     */
    public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    /**
     * Sets the Employee's email adresses.
     * @param emailAddresses Employee's email adresses
     */
    public void setEmailAddresses(List<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
}
