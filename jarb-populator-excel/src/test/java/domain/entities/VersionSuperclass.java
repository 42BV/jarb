package domain.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Mapped superclass to test @Version and abstract inheritance.
 * Only inherited by CompanyVehicle.
 * @author Sander Benschop
 *
 */
@MappedSuperclass
public abstract class VersionSuperclass implements Cloneable {

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Returns the version number.
     * @return Version number
     */
    public Long getVersion() {
        return version;
    }

}
