package domain.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A composite class consisting of foreign keys to Employee, Project and Workspace.
 * As for now, it needs a surrogate key.
 * @author Sander Benschop
 *
 */
@Entity
@Table(name = "employees_projects_workspaces")
public final class EmployeeProjectWorkspace {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "workspace_id", referencedColumnName = "workspace_id", nullable = false)
    private Workspace workspace;

    /**
     * Public constructor.
     */
    public EmployeeProjectWorkspace() {
    }

    /**
     * Returns the record's employee.
     * @return Employee
     */
    public Employee getEmployee() {
        return this.employee;
    }

    /**
     * Returns the record's project.
     * @return Project
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * Returns the record's workspace.
     * @return Workspace
     */
    public Workspace getWorkspace() {
        return this.workspace;
    }

    /**
     * Sets the record's Employee.
     * @param employee Employee
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * Sets the record's Project.
     * @param project Project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Sets the record's Workspace.
     * @param workspace Workspace
     */
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    /**
     * Returns the record's id.
     * @return Identification number for record
     */
    public Long getId() {
        return id;
    }

}
