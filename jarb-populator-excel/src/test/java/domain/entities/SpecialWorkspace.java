package domain.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Extends Workspace, needed for unittesting the default discriminator values.
 * @author Sander Benschop
 *
 */
@Entity
public class SpecialWorkspace extends Workspace {

    @Column(name = "workspace_owner")
    private String workspaceOwner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "department_id")
    private DepartmentSubclass departmentSubclass;

    /**
     * Sets the workspace's workspaceOwner.
     * @param workspaceOwner Workspace's workspaceOwner
     */
    public void setWorkspaceOwner(String workspaceOwner) {
        this.workspaceOwner = workspaceOwner;
    }

    /**
     * Returns the workspace's workspaceOwner.
     * @return Workspace's workspaceOwner
     */
    public String getWorkspaceOwner() {
        return workspaceOwner;
    }

    public void setDepartmentSubclass(DepartmentSubclass departmentSubclass) {
        this.departmentSubclass = departmentSubclass;
    }

    public DepartmentSubclass getDepartmentSubclass() {
        return departmentSubclass;
    }

}
