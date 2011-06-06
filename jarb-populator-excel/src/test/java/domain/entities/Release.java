package domain.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Copied from Panorama project to test if manually settable id fields work.
 * @author Panorama team
 * @author Sander Benschop
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "releases")
public class Release implements Serializable {

    @Id
    private Integer id;

    @Column(name = "release_number", unique = true)
    private Integer number;

    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @Column(name = "release_type")
    private Character type;

    /**
     * Used by Hibernate.
     */
    public Release() {
    }

    public Integer getId() {
        return id;
    }

    /**
     * Returns the release number.
     * @return Release number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Return Date.
     * @return Date
     */
    public Date getDate() {
        return (Date) releaseDate.clone();
    }

    /**
     * Return type character.
     * @return Character
     */
    public Character getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setType(Character type) {
        this.type = type;
    }
}
