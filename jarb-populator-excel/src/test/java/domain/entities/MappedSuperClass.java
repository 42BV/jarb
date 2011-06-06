package domain.entities;

import javax.persistence.MappedSuperclass;

/**
 * Abstract class between Department and DepartmentSubclass.
 * @author Sander Benschop
 *
 */
@MappedSuperclass
public abstract class MappedSuperClass extends Department {

    /** Constructor for Hibernate. */
    public MappedSuperClass() {
        super();
    }

    /** Constructor for hibernate.
     * @param departmentName Name of the department
     */
    public MappedSuperClass(String departmentName) {
        super(departmentName);
    }
}
