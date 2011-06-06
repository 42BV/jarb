package domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Department class is a simple class without any relations.
 * @author Sander Benschop
 *
 */
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @Column(name = "department_id")
    @GeneratedValue
    private Long id;

    @Column(name = "department_name")
    private String departmentName;

    /** Constructor for Hibernate. */
    public Department() {
    }

    /** Constructor for Hibernate. 
     * @param departmentName Name of the department
     */
    public Department(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Set the department id.
     * @param id Id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the department id.
     * @return Department id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the departmentname.
     * @param departmentName Departmentname
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Returns the deparment name.
     * @return Department name
     */
    public String getDepartmentName() {
        return departmentName;
    }

}
