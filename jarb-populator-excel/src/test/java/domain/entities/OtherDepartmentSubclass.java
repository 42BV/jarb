package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Almost the same as DepartmentSubclass but with one extra field. Used to test if duplicate fields from multiple childs are filtered correctly.
 * @author Sander Benschop
 *
 */
@Entity
@DiscriminatorValue("otherabstractsub")
public class OtherDepartmentSubclass extends MappedSuperClass {

    /**
     * The head of the department.
     */
    @Column(name = "department_head")
    private String departmentHead;

    @Column(name = "second_in_command")
    private String secondInCommand;

    /** Constructor for Hibernate. */
    OtherDepartmentSubclass() {
        super();
    }

    /**
     * Constructor with arguments.
     * @param departmentName Name of the department.
     * @param departmentHead Name of the department head.
     * @param secondInCommand Name of the person who's second in command.
     */
    public OtherDepartmentSubclass(String departmentName, String departmentHead, String secondInCommand) {
        super(departmentName);
        this.secondInCommand = secondInCommand;
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

    /**
     * Sets the guy who's second in command.
     * @param secondInCommand Guy who's second in command.
     */
    public void setSecondInCommand(String secondInCommand) {
        this.secondInCommand = secondInCommand;
    }

    /**
     * Returns the guy who's second in command.
     * @return Guy who's second in command.
     */
    public String getSecondInCommand() {
        return secondInCommand;
    }

}
