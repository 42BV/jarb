package nl._42.jarb.constraint.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Embeddable
public class Component {

    private String name;

    @Positive
    private BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
