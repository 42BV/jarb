package domain.entities;

import javax.persistence.Table;

/**
 * Unannotated class needed for unittests, will not be persisted.
 * @author Sander Benschop
 *
 */

@Table(name = "non_existant")
public class UnannotatedClass {

    private Long id;

    private String column;

    /**
     * Sets the id.
     * @param id number
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the id.
     * @return Id number
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the column value.
     * @param value to be set
     */
    public void setColumnValue(String value) {
        this.column = value;
    }

    /**
     * Returns the column's value.
     * @return Column value
     */
    public String getColumnValue() {
        return column;
    }

}
