package nl._42.jarb.constraint.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class Inspection {

    private LocalDate date;

    private String remarks;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
