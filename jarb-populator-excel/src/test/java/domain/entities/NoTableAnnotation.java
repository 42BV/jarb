package domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Class without an @Table annotation, component should generate a table name for it.
 * @author Sander Benschop
 *
 */
@Entity
public class NoTableAnnotation {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "db_column")
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
