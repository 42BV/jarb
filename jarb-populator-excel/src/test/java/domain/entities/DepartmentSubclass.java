package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Subclass of Department through an abstract class MappedSuperClass.
 * @author Sander Benschop
 *
 */
@Entity
@DiscriminatorValue("abstractsub")
public class DepartmentSubclass extends MappedSuperClass {

    /**
     * The head of the department.
     */
    @Column(name = "department_head")
    private String departmentHead;

    /** Constructor for Hibernate. */
    public DepartmentSubclass() {
        super();
    }

    /**
     * Constructor for Hibernate.
     * @param departmentName Name of the department
     * @param departmentHead Name of the department head
     * 
     */
    public DepartmentSubclass(String departmentName, String departmentHead) {
        super(departmentName);
        this.departmentHead = departmentHead;
    }

    /**
     * Sets the department head.
     * @param departmentHead Department head.
     */
    public void setDepartmentHead(String departmentHead) {
        this.departmentHead = departmentHead;
    }

    /**
     * Returns the department head.
     * @return Department head.
     */
    public String getDepartmentHead() {
        return departmentHead;
    }

}
