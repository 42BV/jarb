package domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "currencies")
public class Currency extends CodeBasedEntity {

    @Column(name = "numeric_code")
    private Long numericCode;

    public Long getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(Long numericCode) {
        this.numericCode = numericCode;
    }

}
