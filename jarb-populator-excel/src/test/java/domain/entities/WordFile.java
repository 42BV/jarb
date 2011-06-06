package domain.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Wordfile extends from Document to mimic another Panorama case.
 * In this case a circular reference is present with an abstract class and an implementation.
 * This class is there to test ETD's behaviour with this problem.
 * @author Sander Benschop
 *
 */
@Entity
@DiscriminatorValue("word")
public class WordFile extends Document {

    @Column(name = "summary")
    private String summary;

    /**
     * Sets the Wordfile's summary.
     * @param summary Summary of the contents.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Returns the Wordfile's summary.
     * @return Summary of the contents.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abstractMethod() {
        //Empty!
    }

}
